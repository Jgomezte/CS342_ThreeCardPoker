package edu.uic.cs342.poker.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.io.IOException;

public class WelcomeController {
    @FXML
    private void handleQuitButton() {
        Platform.exit();
    }

    @FXML
    private void handlePlayButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/uic/cs342/poker/fxml/play.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene((new Scene(root, 800, 600)));
    }

    @FXML
    private void handleTutorialButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/uic/cs342/poker/fxml/tutorial.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene((new Scene(root, 800, 600)));
    }
}
