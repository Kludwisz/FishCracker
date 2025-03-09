package com.kludwisz.fishcracker;

import com.kludwisz.fishcracker.measurment.MeasurmentParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class FishCrackerApplication extends Application {
    private final MeasurmentParser measurmentParser = new MeasurmentParser();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FishCrackerApplication.class.getResource("fish-cracker-view.fxml"));
        AnchorPane rootPane = fxmlLoader.load();
        Label angleDisplay = (Label) rootPane.lookup("#angleDisplay");
        Scene scene = new Scene(rootPane, 480, 300);
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
                    // copy clipboard contents to a string
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    String content = clipboard.getString();
                    if (measurmentParser.parseAngleMeasurment(content)) {
                        angleDisplay.setText(content);
                    }
                    else {
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