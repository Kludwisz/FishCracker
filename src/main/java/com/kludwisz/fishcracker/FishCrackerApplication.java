package com.kludwisz.fishcracker;

import com.kludwisz.fishcracker.controls.GlobalKeyListener;
import com.kludwisz.fishcracker.cracker.Cracker;
import com.kludwisz.fishcracker.math.Line;
import com.kludwisz.fishcracker.measurment.MeasurmentParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
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
        FXMLLoader fxmlLoader = new FXMLLoader(FishCrackerApplication.class.getResource("fish-cracker-view.fxml"));
        AnchorPane rootPane = fxmlLoader.load();
        Label angleDisplay = (Label) rootPane.lookup("#angleDisplay");
        this.controller = fxmlLoader.getController();

        Scene scene = new Scene(rootPane, 480, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());

        stage.setTitle("FishCracker v1.0");
        stage.setScene(scene);
        stage.show();

        this.setupF3CListener(angleDisplay);
        rootPane.requestFocus(); // for handling key events
    }

    private void setupF3CListener(Label angleDisplay) {
        GlobalKeyListener f3cListener = new GlobalKeyListener();
        f3cListener.setF3CAction(
                v -> Platform.runLater(() -> {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    String content = clipboard.getString();
                    Line line = measurmentParser.parseMeasurment(content);
                    cracker.addLineConstraint(line);
                    Cracker.StructureModel model = cracker.getStructureModel();
                    if (!model.structures().isEmpty())
                        controller.displayText(model.structures().getFirst().toString());

                    if (line != null) {
                        System.out.println("correct measurment");
                        angleDisplay.setText(line.toString());
                    }
                    else {
                        System.out.println("failed measurment");
                        angleDisplay.setText("...");
                    }
                })
        );
        GlobalKeyListener.register(f3cListener);
    }

    public static void main(String[] args) {
        launch();
    }
}