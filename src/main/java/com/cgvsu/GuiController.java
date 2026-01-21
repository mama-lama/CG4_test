package com.cgvsu;

import com.cgvsu.math.NormalsCalculator;
import com.cgvsu.math.AffineTransformations;
import com.cgvsu.model.Model;
import com.cgvsu.model.ModelOperations;
import com.cgvsu.model.ModelTriangulator;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.RenderSettings;
import com.cgvsu.scene.CameraGizmoFactory;
import com.cgvsu.scene.CameraManager;
import com.cgvsu.scene.SceneCamera;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.cgvsu.math.vectors.Vector3f;

public class GuiController {

    private static final float TRANSLATION = 0.5F;
    private static final float GIZMO_SCALE = 4.0F;

    @FXML
    AnchorPane canvasPane;

    @FXML
    private Canvas canvas;

    @FXML
    private ListView<String> modelListView;

    @FXML
    private ListView<String> cameraListView;

    @FXML
    private Label selectionStatusLabel;

    @FXML
    private Label activeCameraLabel;

    @FXML
    private TextField vertexIndexField;

    @FXML
    private TextField polygonIndexField;

    @FXML
    private TextField translateXField;

    @FXML
    private TextField translateYField;

    @FXML
    private TextField translateZField;

    @FXML
    private TextField rotateXField;

    @FXML
    private TextField rotateYField;

    @FXML
    private TextField rotateZField;

    @FXML
    private TextField scaleXField;

    @FXML
    private TextField scaleYField;

    @FXML
    private TextField scaleZField;

    @FXML
    private CheckMenuItem applyTransformsMenuItem;

    @FXML
    private CheckMenuItem autoPostprocessMenuItem;

    @FXML
    private CheckMenuItem darkThemeMenuItem;

    @FXML
    private CheckMenuItem wireframeMenuItem;

    @FXML
    private CheckMenuItem textureMenuItem;

    @FXML
    private CheckMenuItem lightingMenuItem;

    @FXML
    private CheckMenuItem showCamerasMenuItem;

    @FXML
    private ColorPicker baseColorPicker;

    private Model mesh = null;
    private Image texture = null;
    private String meshName = null;

    private final CameraManager cameraManager = new CameraManager();
    private final RenderSettings renderSettings = new RenderSettings(false, true, true, 0xFFB0B0B0);

    private boolean showCameraGizmos = true;
    private double lastMouseX;
    private double lastMouseY;
    private float orbitYaw = 0.0F;
    private float orbitPitch = 0.0F;
    private float orbitDistance = 100.0F;

    private Timeline timeline;

    @FXML
    private void initialize() {
        if (canvasPane != null) {
            canvas.setWidth(canvasPane.getWidth());
            canvas.setHeight(canvasPane.getHeight());
            canvasPane.widthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
            canvasPane.heightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        }

        setupDefaultCamera();
        setupMouseControls();
        setupKeyboardControls();
        syncUiState();

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            getActiveCamera().setAspectRatio((float) (width / height));

            List<Model> renderModels = new ArrayList<>();
            if (mesh != null) {
                renderModels.add(mesh);
            }
            if (showCameraGizmos) {
                renderModels.addAll(buildCameraGizmos());
            }
            if (!renderModels.isEmpty()) {
                RenderEngine.renderModels(
                        canvas.getGraphicsContext2D(),
                        getActiveCamera(),
                        renderModels,
                        (int) width,
                        (int) height,
                        texture,
                        renderSettings);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            meshName = file.getName();
            if (autoPostprocessMenuItem == null || autoPostprocessMenuItem.isSelected()) {
                postprocessModel();
            }
            syncModelList();
        } catch (IOException exception) {
            // Stub: keep UI minimal for this task set.
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        if (mesh == null) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model As");
        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }
        Model toSave = mesh;
        if (applyTransformsMenuItem != null && applyTransformsMenuItem.isSelected()) {
            toSave = ModelOperations.createTransformedCopy(mesh, new com.cgvsu.math.matrices.Matrix4().identity());
        }
        try {
            ObjWriter.write(toSave, file.toPath());
        } catch (IOException exception) {
            // Stub: keep UI minimal for this task set.
        }
    }

    @FXML
    private void onOpenTextureMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image (*.png, *.jpg, *.jpeg, *.bmp)", "*.png", "*.jpg", "*.jpeg", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        texture = new Image(file.toURI().toString());
    }

    @FXML
    private void onToggleTheme() {
        Scene scene = canvas.getScene();
        if (scene == null) {
            return;
        }
        scene.getStylesheets().clear();
        if (darkThemeMenuItem != null && darkThemeMenuItem.isSelected()) {
            scene.getStylesheets().add("/com/cgvsu/styles/app-dark.css");
        } else {
            scene.getStylesheets().add("/com/cgvsu/styles/app.css");
        }
    }

    @FXML
    private void onRenderSettingsChanged() {
        if (wireframeMenuItem != null) {
            renderSettings.setDrawWireframe(wireframeMenuItem.isSelected());
        }
        if (textureMenuItem != null) {
            renderSettings.setUseTexture(textureMenuItem.isSelected());
        }
        if (lightingMenuItem != null) {
            renderSettings.setUseLighting(lightingMenuItem.isSelected());
        }
        if (showCamerasMenuItem != null) {
            showCameraGizmos = showCamerasMenuItem.isSelected();
        }
        if (baseColorPicker != null) {
            renderSettings.setBaseColor(colorToArgb(baseColorPicker.getValue()));
        }
    }

    @FXML
    private void onPostprocessModelsClick() {
        postprocessModel();
    }

    @FXML
    private void onRemoveModelClick() {
        mesh = null;
        meshName = null;
        syncModelList();
    }

    @FXML
    private void onDeleteVertexClick() {
        if (mesh == null) {
            return;
        }
        int index = parseInt(vertexIndexField);
        if (index < 0) {
            return;
        }
        ModelOperations.deleteVertex(mesh, index);
    }

    @FXML
    private void onDeletePolygonClick() {
        if (mesh == null) {
            return;
        }
        int index = parseInt(polygonIndexField);
        if (index < 0) {
            return;
        }
        ModelOperations.deletePolygon(mesh, index);
    }

    @FXML
    private void onApplyTranslationClick() {
        if (mesh == null) {
            return;
        }
        float x = parseFloat(translateXField, 0.0f);
        float y = parseFloat(translateYField, 0.0f);
        float z = parseFloat(translateZField, 0.0f);
        AffineTransformations.translate(mesh.vertices, x, y, z);
    }

    @FXML
    private void onApplyRotationClick() {
        if (mesh == null) {
            return;
        }
        float x = (float) Math.toRadians(parseFloat(rotateXField, 0.0f));
        float y = (float) Math.toRadians(parseFloat(rotateYField, 0.0f));
        float z = (float) Math.toRadians(parseFloat(rotateZField, 0.0f));
        AffineTransformations.rotate(mesh, x, y, z);
    }

    @FXML
    private void onApplyScaleClick() {
        if (mesh == null) {
            return;
        }
        float x = parseFloat(scaleXField, 1.0f);
        float y = parseFloat(scaleYField, 1.0f);
        float z = parseFloat(scaleZField, 1.0f);
        AffineTransformations.scale(mesh.vertices, x, y, z);
    }

    @FXML
    private void onResetTransformClick() {
        if (translateXField != null) translateXField.setText("");
        if (translateYField != null) translateYField.setText("");
        if (translateZField != null) translateZField.setText("");
        if (rotateXField != null) rotateXField.setText("");
        if (rotateYField != null) rotateYField.setText("");
        if (rotateZField != null) rotateZField.setText("");
        if (scaleXField != null) scaleXField.setText("");
        if (scaleYField != null) scaleYField.setText("");
        if (scaleZField != null) scaleZField.setText("");
    }

    @FXML
    private void onAddCameraClick() {
        addCameraFromActive();
        syncCameraList();
    }

    @FXML
    private void onRemoveCameraClick() {
        cameraManager.removeActive();
        syncCameraList();
    }

    @FXML
    private void onSetActiveCameraClick() {
        if (cameraListView == null) {
            return;
        }
        int index = cameraListView.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= cameraManager.getCameras().size()) {
            return;
        }
        SceneCamera selected = cameraManager.getCameras().get(index);
        cameraManager.setActive(selected);
        syncCameraList();
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        getActiveCamera().movePosition(new Vector3f(0, 0, -TRANSLATION));
        syncOrbitDistance();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        getActiveCamera().movePosition(new Vector3f(0, 0, TRANSLATION));
        syncOrbitDistance();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        getActiveCamera().movePosition(new Vector3f(TRANSLATION, 0, 0));
        syncOrbitDistance();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        getActiveCamera().movePosition(new Vector3f(-TRANSLATION, 0, 0));
        syncOrbitDistance();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        getActiveCamera().movePosition(new Vector3f(0, TRANSLATION, 0));
        syncOrbitDistance();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        getActiveCamera().movePosition(new Vector3f(0, -TRANSLATION, 0));
        syncOrbitDistance();
    }

    private void setupMouseControls() {
        orbitDistance = getActiveCamera().getPosition().sub(getActiveCamera().getTarget()).length();
        canvas.setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
        });
        canvas.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown()) {
                return;
            }
            double deltaX = event.getX() - lastMouseX;
            double deltaY = event.getY() - lastMouseY;
            lastMouseX = event.getX();
            lastMouseY = event.getY();

            float sensitivity = 0.3F;
            orbitYaw += deltaX * sensitivity;
            orbitPitch -= deltaY * sensitivity;
            orbitPitch = Math.max(-89.0F, Math.min(89.0F, orbitPitch));

            updateOrbitCamera();
        });
        canvas.setOnScroll(event -> {
            float zoomSpeed = 0.08F;
            orbitDistance *= (1.0F - event.getDeltaY() * zoomSpeed / 100.0F);
            orbitDistance = Math.max(2.0F, Math.min(500.0F, orbitDistance));
            updateOrbitCamera();
        });
    }

    private void setupKeyboardControls() {
        canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                return;
            }
            newScene.setOnKeyPressed(event -> handleKeyPressed(newScene, event.getCode()));
        });
    }

    private void handleKeyPressed(Scene scene, KeyCode code) {
        switch (code) {
            case UP -> handleCameraForward(null);
            case DOWN -> handleCameraBackward(null);
            case LEFT -> handleCameraLeft(null);
            case RIGHT -> handleCameraRight(null);
            case TAB -> cameraManager.cycleActive();
            case C -> addCameraFromActive();
            case DELETE -> cameraManager.removeActive();
            case W -> renderSettings.setDrawWireframe(!renderSettings.isDrawWireframe());
            case T -> renderSettings.setUseTexture(!renderSettings.isUseTexture());
            case L -> renderSettings.setUseLighting(!renderSettings.isUseLighting());
            case G -> showCameraGizmos = !showCameraGizmos;
            case P -> postprocessModel();
            default -> {
            }
        }
        syncOrbitDistance();
    }

    private void postprocessModel() {
        if (mesh == null) {
            return;
        }
        ModelTriangulator.triangulate(mesh);
        NormalsCalculator.recalculateNormals(mesh);
        fitModelToView();
    }

    private Camera getActiveCamera() {
        SceneCamera active = cameraManager.getActive();
        return active == null ? null : active.getCamera();
    }

    private void setupDefaultCamera() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F,
                1,
                0.01F,
                100);
        SceneCamera defaultCamera = new SceneCamera(camera, "Camera 1", CameraGizmoFactory.createGizmo());
        cameraManager.add(defaultCamera);
        syncCameraList();
    }

    private void addCameraFromActive() {
        SceneCamera active = cameraManager.getActive();
        if (active == null) {
            return;
        }
        Camera source = active.getCamera();
        Camera copy = new Camera(
                new Vector3f(source.getPosition().getX(), source.getPosition().getY(), source.getPosition().getZ()),
                new Vector3f(source.getTarget().getX(), source.getTarget().getY(), source.getTarget().getZ()),
                source.getFov(),
                source.getAspectRatio(),
                source.getNearPlane(),
                source.getFarPlane());
        SceneCamera newCamera = new SceneCamera(
                copy,
                "Camera " + (cameraManager.getCameras().size() + 1),
                CameraGizmoFactory.createGizmo());
        cameraManager.add(newCamera);
        cameraManager.setActive(newCamera);
        syncCameraList();
    }

    private List<Model> buildCameraGizmos() {
        List<Model> gizmos = new ArrayList<>();
        SceneCamera active = cameraManager.getActive();
        for (SceneCamera sceneCamera : cameraManager.getCameras()) {
            if (sceneCamera == active) {
                continue;
            }
            Vector3f position = sceneCamera.getCamera().getPosition();
            gizmos.add(CameraGizmoFactory.createGizmoAt(position, GIZMO_SCALE));
        }
        return gizmos;
    }

    private void updateOrbitCamera() {
        Vector3f target = getActiveCamera().getTarget();
        double yawRad = Math.toRadians(orbitYaw);
        double pitchRad = Math.toRadians(orbitPitch);

        float x = (float) (target.getX() + orbitDistance * Math.cos(pitchRad) * Math.sin(yawRad));
        float y = (float) (target.getY() + orbitDistance * Math.sin(pitchRad));
        float z = (float) (target.getZ() + orbitDistance * Math.cos(pitchRad) * Math.cos(yawRad));
        getActiveCamera().setPosition(new Vector3f(x, y, z));
    }

    private void syncOrbitDistance() {
        Vector3f delta = getActiveCamera().getPosition().sub(getActiveCamera().getTarget());
        orbitDistance = delta.length();
    }

    private void fitModelToView() {
        if (mesh == null || mesh.vertices.isEmpty()) {
            return;
        }

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;

        for (Vector3f vertex : mesh.vertices) {
            minX = Math.min(minX, vertex.getX());
            minY = Math.min(minY, vertex.getY());
            minZ = Math.min(minZ, vertex.getZ());
            maxX = Math.max(maxX, vertex.getX());
            maxY = Math.max(maxY, vertex.getY());
            maxZ = Math.max(maxZ, vertex.getZ());
        }

        float centerX = (minX + maxX) * 0.5f;
        float centerY = (minY + maxY) * 0.5f;
        float centerZ = (minZ + maxZ) * 0.5f;

        float maxLen = 0.0f;
        for (Vector3f vertex : mesh.vertices) {
            float x = vertex.getX() - centerX;
            float y = vertex.getY() - centerY;
            float z = vertex.getZ() - centerZ;
            vertex.set(x, y, z);
            maxLen = Math.max(maxLen, vertex.length());
        }

        if (maxLen < 1e-5f) {
            return;
        }

        float desiredRadius = 30.0f;
        float scale = maxLen > desiredRadius ? desiredRadius / maxLen : 1.0f;
        if (scale != 1.0f) {
            for (Vector3f vertex : mesh.vertices) {
                vertex.set(vertex.getX() * scale, vertex.getY() * scale, vertex.getZ() * scale);
            }
            maxLen *= scale;
        }

        Camera activeCamera = getActiveCamera();
        if (activeCamera != null) {
            activeCamera.setTarget(new Vector3f(0, 0, 0));
            float distance = Math.max(3.0f, maxLen * 3.0f);
            activeCamera.setPosition(new Vector3f(0, 0, distance));
            syncOrbitDistance();
        }
    }

    private void syncUiState() {
        if (wireframeMenuItem != null) {
            wireframeMenuItem.setSelected(renderSettings.isDrawWireframe());
        }
        if (textureMenuItem != null) {
            textureMenuItem.setSelected(renderSettings.isUseTexture());
        }
        if (lightingMenuItem != null) {
            lightingMenuItem.setSelected(renderSettings.isUseLighting());
        }
        if (showCamerasMenuItem != null) {
            showCamerasMenuItem.setSelected(showCameraGizmos);
        }
        if (baseColorPicker != null) {
            baseColorPicker.setValue(argbToColor(renderSettings.getBaseColor()));
        }
        syncModelList();
        syncCameraList();
    }

    private void syncModelList() {
        if (modelListView != null) {
            modelListView.getItems().clear();
            if (mesh != null) {
                modelListView.getItems().add(meshName == null ? "Model" : meshName);
            }
        }
        if (selectionStatusLabel != null) {
            selectionStatusLabel.setText("Выбрано моделей: " + (mesh == null ? 0 : 1));
        }
    }

    private void syncCameraList() {
        if (cameraListView != null) {
            cameraListView.getItems().clear();
            for (SceneCamera camera : cameraManager.getCameras()) {
                cameraListView.getItems().add(camera.getName());
            }
            SceneCamera active = cameraManager.getActive();
            if (active != null) {
                cameraListView.getSelectionModel().select(active.getName());
            }
        }
        if (activeCameraLabel != null) {
            SceneCamera active = cameraManager.getActive();
            String name = active == null ? "None" : active.getName();
            activeCameraLabel.setText("Активная камера: " + name);
        }
    }

    private static int parseInt(TextField field) {
        if (field == null) {
            return -1;
        }
        String text = field.getText();
        if (text == null || text.isBlank()) {
            return -1;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private static float parseFloat(TextField field, float defaultValue) {
        if (field == null) {
            return defaultValue;
        }
        String text = field.getText();
        if (text == null || text.isBlank()) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(text.trim());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    private static int colorToArgb(Color color) {
        if (color == null) {
            return 0xFFFFFFFF;
        }
        int a = (int) Math.round(color.getOpacity() * 255.0);
        int r = (int) Math.round(color.getRed() * 255.0);
        int g = (int) Math.round(color.getGreen() * 255.0);
        int b = (int) Math.round(color.getBlue() * 255.0);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static Color argbToColor(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return Color.rgb(r, g, b, a / 255.0);
    }
}
