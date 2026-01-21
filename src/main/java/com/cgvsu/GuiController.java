package com.cgvsu;

import com.cgvsu.math.NormalsCalculator;
import com.cgvsu.model.Model;
import com.cgvsu.model.ModelOperations;
import com.cgvsu.model.ModelTriangulator;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
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

import javax.vecmath.Vector3f;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    private Button removeModelButton;

    private final ObservableList<SceneModel> models = FXCollections.observableArrayList();
    private Image texture = null;

    private final Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        canvasPane.widthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        canvasPane.heightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        modelListView.setItems(models);
        modelListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        modelListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateSelectionStatus());

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (!models.isEmpty()) {
                List<Model> renderModels = new ArrayList<>();
                for (SceneModel sceneModel : models) {
                    renderModels.add(sceneModel.getModel());
                }
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, renderModels, (int) width, (int) height, texture);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        updateSelectionStatus();
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

        Path filePath = file.toPath();

        try {
            String fileContent = Files.readString(filePath);
            Model mesh = ObjReader.read(fileContent);
            ModelTriangulator.triangulate(mesh);
            NormalsCalculator.recalculateNormals(mesh);
            SceneModel sceneModel = new SceneModel(mesh, file.getName(), filePath);
            models.add(sceneModel);
            modelListView.getSelectionModel().clearSelection();
            modelListView.getSelectionModel().select(sceneModel);
        } catch (ObjReaderException exception) {
            showError("Invalid OBJ file", exception.getMessage());
        } catch (IOException exception) {
            showError("Failed to read file", exception.getMessage());
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        SceneModel active = getSingleSelection();
        if (active == null) {
            showError("Save Model", "Select exactly one model to save.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model As");
        fileChooser.setInitialFileName(active.getName());

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            ObjWriter.write(active.getModel(), file.toPath());
        } catch (IOException exception) {
            showError("Failed to save model", exception.getMessage());
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
    private void onRemoveModelClick() {
        List<SceneModel> selected = new ArrayList<>(modelListView.getSelectionModel().getSelectedItems());
        if (selected.isEmpty()) {
            showError("Remove Model", "Select at least one model to remove.");
            return;
        }
        models.removeAll(selected);
        updateSelectionStatus();
    }

    @FXML
    private void onDeleteVertexClick() {
        Integer vertexIndex = parseIndex(vertexIndexField.getText());
        if (vertexIndex == null) {
            showError("Delete Vertex", "Enter a valid vertex index (0-based).");
            return;
        }
        List<SceneModel> selected = modelListView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            showError("Delete Vertex", "Select at least one model.");
            return;
        }
        boolean any = false;
        for (SceneModel sceneModel : selected) {
            any |= ModelOperations.deleteVertex(sceneModel.getModel(), vertexIndex);
        }
        if (!any) {
            showError("Delete Vertex", "Vertex index is out of range for selected models.");
        }
    }

    @FXML
    private void onDeletePolygonClick() {
        Integer polygonIndex = parseIndex(polygonIndexField.getText());
        if (polygonIndex == null) {
            showError("Delete Polygon", "Enter a valid polygon index (0-based).");
            return;
        }
        List<SceneModel> selected = modelListView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            showError("Delete Polygon", "Select at least one model.");
            return;
        }
        boolean any = false;
        for (SceneModel sceneModel : selected) {
            any |= ModelOperations.deletePolygon(sceneModel.getModel(), polygonIndex);
        }
        if (!any) {
            showError("Delete Polygon", "Polygon index is out of range for selected models.");
        }
    }

    @FXML
    private void onToggleTheme() {
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
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
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

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateSelectionStatus() {
        int selectedCount = modelListView.getSelectionModel().getSelectedItems().size();
        selectionStatusLabel.setText("Selected models: " + selectedCount);
        removeModelButton.setDisable(selectedCount == 0);
    }
}
