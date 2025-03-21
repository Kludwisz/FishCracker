package com.kludwisz.fishcracker;

import com.kludwisz.fishcracker.cracker.Cracker;
import com.kludwisz.fishcracker.cracker.LikelyStructure;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FishCrackerController {
    @FXML private Label angleDisplay;
    @FXML private VBox structureContainer;
    @FXML private Label collectedInfoLabel;
    @FXML private ProgressBar collectedInfoBar;
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
        collectedInfoBar.setProgress(Math.clamp(bits / 48.0, 0.0, 1.0));
    }

    public void displayModel(Cracker.StructureModel model) {
        this.structureContainer.getChildren().clear();
        this.updateCollectedInfo(model.bits());

        for (int i = 0; i < model.structures().size(); i++) {
            LikelyStructure structure = model.structures().get(i);
            HBox entry = new HBox();
            entry.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Button removeButton = new Button("X");
            removeButton.setOnAction(e -> this.removeStructureEntry(structureContainer.getChildren().indexOf(entry)));
            removeButton.setMaxWidth(20.0);
            entry.getChildren().add(removeButton);

            Label label = new Label(structure.toString());
            entry.getChildren().add(label);

            this.addStructureEntry(entry);
        }
    }
}
