package com.kludwisz.fishcracker;

import com.kludwisz.fishcracker.cracker.Cracker;
import com.kludwisz.fishcracker.measurment.MeasurmentParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FishCrackerApplication extends Application {
    private FishCrackerController controller;

    private final Cracker cracker = new Cracker();
    private final MeasurmentParser measurmentParser = new MeasurmentParser();

    @Override
    public void start(Stage stage) throws IOException {
        // load icon resource
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("app_icon.png"))));

        FXMLLoader fxmlLoader = new FXMLLoader(FishCrackerApplication.class.getResource("fish-cracker-view.fxml"));
        AnchorPane rootPane = fxmlLoader.load();
        this.controller = fxmlLoader.getController();
        this.controller.bindCrackerInstance(cracker);

        Scene scene = new Scene(rootPane, 480, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());

        stage.setTitle("FishCracker v1.0");
        stage.setScene(scene);
        stage.show();

        // set custom app exit behavior
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}