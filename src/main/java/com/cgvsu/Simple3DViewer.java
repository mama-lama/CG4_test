package com.cgvsu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Simple3DViewer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Point 4: bootstrap UI and theme.
        Parent viewport = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/gui.fxml")));

        Scene scene = new Scene(viewport);
        scene.getStylesheets().add("/com/cgvsu/styles/app.css");
        stage.setWidth(1200);
        stage.setHeight(750);
        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        stage.setTitle("Просмотрщик 3D");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
