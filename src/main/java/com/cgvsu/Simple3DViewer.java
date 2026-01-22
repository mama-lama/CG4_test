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
        stage.setMaximized(true);

        stage.setTitle("\u041f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0449\u0438\u043a 3D");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
