package edu.uic.cs342.poker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private void handleStartButton() {
        welcomeLabel.setText("get started");
    }
}
