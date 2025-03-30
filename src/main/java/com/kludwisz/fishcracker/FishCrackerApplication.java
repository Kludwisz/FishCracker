package com.kludwisz.fishcracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FishCrackerApplication extends Application {
    public static final String NAME = "FishCracker v1.1";

    @Override
    public void start(Stage stage) throws IOException {
        // load icon resource
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("app_icon.png"))));

        FXMLLoader fxmlLoader = new FXMLLoader(FishCrackerApplication.class.getResource("fish-cracker-view.fxml"));
        AnchorPane rootPane = fxmlLoader.load();

        Scene scene = new Scene(rootPane, 480, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());

        stage.setTitle(FishCrackerApplication.NAME);
        stage.setScene(scene);
        stage.show();
    }
}