package com.kludwisz.fishcracker;

import com.kludwisz.fishcracker.cracker.Cracker;
import com.kludwisz.fishcracker.cracker.CrackingFailedException;
import com.kludwisz.fishcracker.cracker.LikelyStructure;
import com.kludwisz.fishcracker.math.Line;
import com.kludwisz.fishcracker.measurment.MeasurmentParser;
import com.seedfinding.mccore.util.math.NextLongReverser;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FishCrackerController {
    private final Cracker cracker = new Cracker();
    private final MeasurmentParser measurmentParser = new MeasurmentParser();

    @FXML private Label angleDisplay;
    @FXML private VBox structureContainer;
    @FXML private Label collectedInfoLabel;
    @FXML private ProgressBar collectedInfoBar;

    @FXML private void addMeasurementAction() {
        try {
            // get text from system clipboard
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            String txt = (String)cb.getData(DataFlavor.stringFlavor);

            Line line = measurmentParser.parseMeasurment(txt);
            if (line == null)
                throw new IllegalArgumentException("Invalid angle");

            if (angleDisplay.getText().equals(txt))
                return;

            angleDisplay.setText(txt);
            cracker.addLineConstraint(line);
            this.displayModel(cracker.getStructureModel());
        }
        catch (IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid angle");
            alert.setHeaderText("The angle you entered is invalid.");
            alert.showAndWait();
        }
        catch (IOException | UnsupportedFlavorException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not read clipboard");
            alert.setHeaderText("The application could not access your clipboard.");
            alert.showAndWait();
        }
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

    @FXML private void runCrackerAction() {
        try {
            List<Long> worldseeds = cracker.getStructreSeeds().stream()
                    .flatMap(seed -> NextLongReverser.getNextLongEquivalents(seed).stream())
                    .filter(cracker::testFullWorldSeed)
                    .toList();
            if (worldseeds.isEmpty())
                throw new CrackingFailedException("No valid world seeds found");

            StringBuilder result = new StringBuilder();
            for (long seed : worldseeds)
                result.append(seed).append("\n");
            this.copyToClipboard(result.toString());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success!");
            alert.setHeaderText("Found " + worldseeds.size() + " valid world seeds:");
            alert.setContentText("The world seeds have been copied to your clipboard. Click OK to save them to a file.");
            Optional<ButtonType> saveResult = alert.showAndWait();

            if (saveResult.isPresent() && saveResult.get() == ButtonType.OK) {
                // Save results to user-chosen file
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save world seeds to file");
                File savefile = fileChooser.showSaveDialog(structureContainer.getScene().getWindow());

                try (FileWriter fout = new FileWriter(savefile)) {
                    fout.write(result.toString());
                }
                catch(Exception e) {
                    Alert saveError = new Alert(Alert.AlertType.ERROR);
                    saveError.setTitle("Error saving world seeds");
                    saveError.setHeaderText("An error occurred while saving the world seeds to file: " + e.getMessage());
                    saveError.showAndWait();
                }
            }
        }
        catch (CrackingFailedException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Seed cracking error");
            alert.setHeaderText("Something went wrong: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void copyToClipboard(String string) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(string), null);
    }

    public void addStructureEntry(HBox entry) {
        structureContainer.getChildren().add(entry);
    }

    public void removeStructureEntry(int index) {
        if (index >= 0 && index < structureContainer.getChildren().size()) {
            structureContainer.getChildren().remove(index);
            cracker.removeStructureFromModel(index);
        }
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

    @FXML public void helpMenu() {
        Alert saveError = new Alert(Alert.AlertType.INFORMATION);
        saveError.setTitle("Help");
        saveError.setHeaderText("FishCracker v1.0");
        saveError.setContentText(
                """
                Give a dolphin raw fish and measure the angle using F3+C.
                Use the "Add Measurement" button to input your last measurement into the cracker.
                Once you've collected enough information (~46 bits or more),
                click "Actions -> Run Cracker" to find possible world seeds.
                """
        );
        saveError.showAndWait();
    }

    @FXML public void aboutMenu() {
        Alert saveError = new Alert(Alert.AlertType.INFORMATION);
        saveError.setTitle("About");
        saveError.setHeaderText("FishCracker v1.0");
        saveError.setContentText("a silly April Fools project that lets you\ncrack Minecraft seeds using raw fish and dolphins.");
        saveError.showAndWait();
    }
}
