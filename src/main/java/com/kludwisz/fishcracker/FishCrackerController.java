package com.kludwisz.fishcracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FishCrackerController {
    @FXML public Label angleDisplay;
    @FXML public VBox structureContainer;
    @FXML public Label collectedInfoLabel;
    @FXML private Label resultDisplay;

    public void displayText(String result) {
        resultDisplay.setText(result);
    }

    public void addStructureEntry(HBox entry) {
        structureContainer.getChildren().add(entry);
    }

    public void removeStructureEntry(int index) {
        if (index >= 0 && index < structureContainer.getChildren().size())
            structureContainer.getChildren().remove(index);
    }

    public void updateCollectedInfo(double bits) {
        collectedInfoLabel.setText("collected information: " + Math.round(bits * 10.0) / 10.0 + " bits");
    }
}
