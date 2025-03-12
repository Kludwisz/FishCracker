package com.kludwisz.fishcracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FishCrackerController {
    @FXML
    private Label resultDisplay;

    public void displayText(String result) {
        resultDisplay.setText(result);
    }
}
