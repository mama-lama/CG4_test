package com.cgvsu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.NormalsCalculator;
import com.cgvsu.math.matrices.Matrix4;
import com.cgvsu.math.vectors.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.ModelOperations;
import com.cgvsu.model.ModelTriangulator;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.RenderSettings;
import com.cgvsu.scene.CameraGizmoFactory;
import com.cgvsu.scene.SceneCamera;
import com.cgvsu.scene.SceneModel;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GuiController {
    private static final float TRANSLATION = 0.5F;
    private static final String LIGHT_THEME = "/com/cgvsu/styles/app.css";
    private static final String DARK_THEME = "/com/cgvsu/styles/app-dark.css";

    @FXML
    BorderPane rootPane;

    @FXML
    private AnchorPane canvasPane;

    @FXML
    private VBox sidePanel;

    @FXML
    private Canvas canvas;

    @FXML
    private ListView<SceneModel> modelListView;

    @FXML
    private TextField vertexIndexField;

    @FXML
    private TextField polygonIndexField;

    @FXML
    private Label selectionStatusLabel;

    @FXML
    private CheckMenuItem darkThemeMenuItem;

    @FXML
    private CheckMenuItem applyTransformsMenuItem;

    @FXML
    private CheckMenuItem autoPostprocessMenuItem;

    @FXML
    private CheckMenuItem wireframeMenuItem;

    @FXML
    private CheckMenuItem textureMenuItem;

    @FXML
    private CheckMenuItem lightingMenuItem;

    @FXML
    private CheckMenuItem showCamerasMenuItem;

    @FXML
    private Button removeModelButton;

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
    private ColorPicker baseColorPicker;

    @FXML
    private ListView<SceneCamera> cameraListView;

    @FXML
    private Label activeCameraLabel;

    private final ObservableList<SceneModel> models = FXCollections.observableArrayList();
    private final ObservableList<SceneCamera> cameras = FXCollections.observableArrayList();
    private Image texture = null;

    private SceneCamera activeCamera;

    private Timeline timeline;
    private double lastMouseX;
    private double lastMouseY;
    private float orbitYaw = 0.0F;
    private float orbitPitch = 0.0F;
    private float orbitDistance = 100.0F;
    private RenderSettings renderSettings = new RenderSettings(false, true, true, 0xFFB0B0B0);

    @FXML
    private void initialize() {
        // Point 2/4/15/16: wire UI, render modes, and cameras.
        canvasPane.widthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        canvasPane.heightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        modelListView.setItems(models);
        modelListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        modelListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateSelectionStatus());

        cameraListView.setItems(cameras);
        setupDefaultCamera();

        if (autoPostprocessMenuItem != null) {
            autoPostprocessMenuItem.setSelected(true);
        }
        if (wireframeMenuItem != null) {
            wireframeMenuItem.setSelected(renderSettings.isDrawWireframe());
        }
        if (textureMenuItem != null) {
            textureMenuItem.setSelected(renderSettings.isUseTexture());
        }
        if (lightingMenuItem != null) {
            lightingMenuItem.setSelected(renderSettings.isUseLighting());
        }
        if (baseColorPicker != null) {
            baseColorPicker.setValue(javafx.scene.paint.Color.web("#B0B0B0"));
        }
        onRenderSettingsChanged();

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            getActiveCamera().setAspectRatio((float) (width / height));

            ArrayList<SceneModel> renderModels = new ArrayList<>(models);
            if (showCamerasMenuItem != null && showCamerasMenuItem.isSelected()) {
                renderModels.addAll(buildCameraSceneModels());
            }
            if (!renderModels.isEmpty()) {
                RenderEngine.renderSceneModels(
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

        setupMouseControls();
        setupKeyboardControls();
        updateSelectionStatus();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        // Point 1/12: load model, optional triangulation and normals.
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Загрузить модель");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path filePath = file.toPath();

        try {
            String fileContent = Files.readString(filePath);
            Model mesh = ObjReader.read(fileContent);
            if (autoPostprocessMenuItem == null || autoPostprocessMenuItem.isSelected()) {
                ModelTriangulator.triangulate(mesh);
                NormalsCalculator.recalculateNormals(mesh);
            }
            SceneModel sceneModel = new SceneModel(mesh, file.getName(), filePath);
            models.add(sceneModel);
            modelListView.getSelectionModel().clearSelection();
            modelListView.getSelectionModel().select(sceneModel);
        } catch (ObjReaderException exception) {
            showError("Ошибка OBJ", exception.getMessage());
        } catch (IOException exception) {
            showError("Ошибка чтения", exception.getMessage());
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        // Point 1/10: save original or transformed model.
        SceneModel active = getSingleSelection();
        if (active == null) {
            showError("Сохранение", "Выберите ровно одну модель.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Сохранить как");
        fileChooser.setInitialFileName(active.getName());

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            if (applyTransformsMenuItem != null && applyTransformsMenuItem.isSelected()) {
                Matrix4 modelMatrix = GraphicConveyor.rotateScaleTranslate(
                        active.getTranslation(), active.getRotation(), active.getScale());
                Model transformed = ModelOperations.createTransformedCopy(active.getModel(), modelMatrix);
                NormalsCalculator.recalculateNormals(transformed);
                ObjWriter.write(transformed, file.toPath());
            } else {
                ObjWriter.write(active.getModel(), file.toPath());
            }
        } catch (IOException exception) {
            showError("Ошибка сохранения", exception.getMessage());
        }
    }

    @FXML
    private void onOpenTextureMenuItemClick() {
        // Point 14: load texture for rendering.
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image (*.png, *.jpg, *.jpeg, *.bmp)", "*.png", "*.jpg", "*.jpeg", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setTitle("Загрузить текстуру");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        texture = new Image(file.toURI().toString());
    }

    @FXML
    private void onRemoveModelClick() {
        // Point 2: remove selected models from the scene.
        List<SceneModel> selected = new ArrayList<>(modelListView.getSelectionModel().getSelectedItems());
        if (selected.isEmpty()) {
            showError("Удаление модели", "Выберите хотя бы одну модель.");
            return;
        }
        models.removeAll(selected);
        updateSelectionStatus();
    }

    @FXML
    private void onDeleteVertexClick() {
        // Point 3: delete a vertex in selected models.
        Integer vertexIndex = parseIndex(vertexIndexField.getText());
        if (vertexIndex == null) {
            showError("Удаление вершины", "Введите корректный индекс вершины.");
            return;
        }
        List<SceneModel> selected = modelListView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            showError("Удаление вершины", "Выберите хотя бы одну модель.");
            return;
        }
        boolean any = false;
        for (SceneModel sceneModel : selected) {
            any |= ModelOperations.deleteVertex(sceneModel.getModel(), vertexIndex);
        }
        if (!any) {
            showError("Удаление вершины", "Индекс вершины вне диапазона.");
        }
    }

    @FXML
    private void onDeletePolygonClick() {
        // Point 3: delete a polygon in selected models.
        Integer polygonIndex = parseIndex(polygonIndexField.getText());
        if (polygonIndex == null) {
            showError("Удаление полигона", "Введите корректный индекс полигона.");
            return;
        }
        List<SceneModel> selected = modelListView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            showError("Удаление полигона", "Выберите хотя бы одну модель.");
            return;
        }
        boolean any = false;
        for (SceneModel sceneModel : selected) {
            any |= ModelOperations.deletePolygon(sceneModel.getModel(), polygonIndex);
        }
        if (!any) {
            showError("Удаление полигона", "Индекс полигона вне диапазона.");
        }
    }

    @FXML
    private void onApplyTranslationClick() {
        // Point 10: apply translation to selected models.
        Vector3f translation = new Vector3f(
                parseFloatOr(translateXField.getText(), 0.0F),
                parseFloatOr(translateYField.getText(), 0.0F),
                parseFloatOr(translateZField.getText(), 0.0F));
        applyToSelection(sceneModel -> sceneModel.translate(translation));
    }

    @FXML
    private void onApplyRotationClick() {
        // Point 10: apply rotation to selected models.
        Vector3f rotation = new Vector3f(
                parseFloatOr(rotateXField.getText(), 0.0F),
                parseFloatOr(rotateYField.getText(), 0.0F),
                parseFloatOr(rotateZField.getText(), 0.0F));
        applyToSelection(sceneModel -> sceneModel.rotate(rotation));
    }

    @FXML
    private void onApplyScaleClick() {
        // Point 10: apply scale to selected models.
        Vector3f scale = new Vector3f(
                parseFloatOr(scaleXField.getText(), 1.0F),
                parseFloatOr(scaleYField.getText(), 1.0F),
                parseFloatOr(scaleZField.getText(), 1.0F));
        applyToSelection(sceneModel -> sceneModel.scale(scale));
    }

    @FXML
    private void onResetTransformClick() {
        // Point 10: reset model transforms.
        applyToSelection(SceneModel::resetTransform);
    }

    @FXML
    private void onPostprocessModelsClick() {
        // Point 12: manual triangulation and normals recompute.
        List<SceneModel> selected = modelListView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            showError("Постобработка", "Выберите хотя бы одну модель.");
            return;
        }
        for (SceneModel sceneModel : selected) {
            ModelTriangulator.triangulate(sceneModel.getModel());
            NormalsCalculator.recalculateNormals(sceneModel.getModel());
        }
    }

    @FXML
    private void onRenderSettingsChanged() {
        // Point 15: update render mode toggles and base color.
        if (wireframeMenuItem != null) {
            renderSettings.setDrawWireframe(wireframeMenuItem.isSelected());
        }
        if (textureMenuItem != null) {
            renderSettings.setUseTexture(textureMenuItem.isSelected());
        }
        if (lightingMenuItem != null) {
            renderSettings.setUseLighting(lightingMenuItem.isSelected());
        }
        if (baseColorPicker != null) {
            javafx.scene.paint.Color color = baseColorPicker.getValue();
            renderSettings.setBaseColor(toArgb(color));
        }
    }

    @FXML
    private void onAddCameraClick() {
        // Point 16: create a new camera from the active one.
        Camera source = getActiveCamera();
        Camera copy = new Camera(
                new Vector3f(source.getPosition().getX(), source.getPosition().getY(), source.getPosition().getZ()),
                new Vector3f(source.getTarget().getX(), source.getTarget().getY(), source.getTarget().getZ()),
                source.getFov(),
                source.getAspectRatio(),
                source.getNearPlane(),
                source.getFarPlane());
        SceneCamera sceneCamera = new SceneCamera(copy, "Камера " + (cameras.size() + 1), CameraGizmoFactory.createGizmo());
        cameras.add(sceneCamera);
        cameraListView.getSelectionModel().select(sceneCamera);
    }

    @FXML
    private void onRemoveCameraClick() {
        // Point 16: remove camera and keep a valid active camera.
        SceneCamera selected = cameraListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Камеры", "Выберите камеру для удаления.");
            return;
        }
        cameras.remove(selected);
        if (cameras.isEmpty()) {
            setupDefaultCamera();
            return;
        }
        if (activeCamera == selected) {
            setActiveCamera(cameras.get(0));
        }
    }

    @FXML
    private void onSetActiveCameraClick() {
        // Point 16: switch active camera.
        SceneCamera selected = cameraListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Камеры", "Выберите камеру для активации.");
            return;
        }
        setActiveCamera(selected);
    }

    @FXML
    private void onToggleTheme() {
        // Point 4: runtime theme switching.
        Scene scene = canvas.getScene();
        if (scene == null) {
            return;
        }
        scene.getStylesheets().clear();
        String theme = darkThemeMenuItem.isSelected() ? DARK_THEME : LIGHT_THEME;
        scene.getStylesheets().add(theme);
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

    private SceneModel getSingleSelection() {
        List<SceneModel> selected = modelListView.getSelectionModel().getSelectedItems();
        if (selected.size() != 1) {
            return null;
        }
        return selected.get(0);
    }

    private Integer parseIndex(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private float parseFloatOr(String text, float fallback) {
        if (text == null || text.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Float.parseFloat(text.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void applyToSelection(java.util.function.Consumer<SceneModel> action) {
        List<SceneModel> selected = modelListView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            showError("Трансформация", "Выберите хотя бы одну модель.");
            return;
        }
        for (SceneModel sceneModel : selected) {
            action.accept(sceneModel);
        }
    }

    private void showError(String header, String message) {
        // Point 5: show user-facing error dialogs.
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateSelectionStatus() {
        int selectedCount = modelListView.getSelectionModel().getSelectedItems().size();
        selectionStatusLabel.setText("Выбрано моделей: " + selectedCount);
        removeModelButton.setDisable(selectedCount == 0);
    }

    private void setupMouseControls() {
        // Point 11: mouse orbit + zoom controls.
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
        // Point 11: allow camera movement with arrow keys even without menu focus.
        canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                return;
            }
            newScene.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case UP -> handleCameraForward(null);
                    case DOWN -> handleCameraBackward(null);
                    case LEFT -> handleCameraLeft(null);
                    case RIGHT -> handleCameraRight(null);
                    default -> {
                    }
                }
            });
        });
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
        orbitDistance = getActiveCamera().getPosition().sub(getActiveCamera().getTarget()).length();
    }

    private Camera getActiveCamera() {
        return activeCamera.getCamera();
    }

    private void setupDefaultCamera() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100);
        SceneCamera defaultCamera = new SceneCamera(camera, "Камера 1", CameraGizmoFactory.createGizmo());
        cameras.add(defaultCamera);
        setActiveCamera(defaultCamera);
    }

    private void setActiveCamera(SceneCamera camera) {
        activeCamera = camera;
        if (activeCameraLabel != null) {
            activeCameraLabel.setText("Активная камера: " + camera.getName());
        }
        cameraListView.getSelectionModel().select(camera);
        syncOrbitDistance();
    }

    private int toArgb(javafx.scene.paint.Color color) {
        int a = (int) Math.round(color.getOpacity() * 255.0);
        int r = (int) Math.round(color.getRed() * 255.0);
        int g = (int) Math.round(color.getGreen() * 255.0);
        int b = (int) Math.round(color.getBlue() * 255.0);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private List<SceneModel> buildCameraSceneModels() {
        List<SceneModel> cameraModels = new ArrayList<>();
        for (SceneCamera sceneCamera : cameras) {
            SceneModel model = new SceneModel(sceneCamera.getGizmoModel(), sceneCamera.getName(), null);
            model.getTranslation().set(
                    sceneCamera.getPosition().getX(),
                    sceneCamera.getPosition().getY(),
                    sceneCamera.getPosition().getZ());
            model.getScale().set(4, 4, 4);
            cameraModels.add(model);
        }
        return cameraModels;
    }
}




