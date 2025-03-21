package com.kludwisz.fishcracker;

import com.kludwisz.fishcracker.cracker.Cracker;
import com.kludwisz.fishcracker.cracker.LikelyStructure;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow;

import java.util.Optional;

public class FishCrackerController {
    @FXML private Label angleDisplay;
    @FXML private VBox structureContainer;
    @FXML private Label collectedInfoLabel;
    @FXML private ProgressBar collectedInfoBar;
    @FXML private Label resultDisplay;
    private Cracker cracker;


    public void bindCrackerInstance(Cracker cracker) {
        this.cracker = cracker;
    }

    @FXML private void resetCrackerAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Reset");
        alert.setHeaderText("Are you absolutely, 100%, undeniably sure you want to reset the cracker?");
        alert.setContentText("This action is NOT reversible (unless you have a time machine or are faster than Java's garbage collector, which honestly wouldn't be surprising).");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.cracker.reset();
        }
    }


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
        collectedInfoLabel.setText("collected information:    " + Math.round(bits * 10.0) / 10.0 + " bits    ");
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
