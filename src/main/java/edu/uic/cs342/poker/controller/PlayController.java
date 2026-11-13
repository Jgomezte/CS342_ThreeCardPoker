package edu.uic.cs342.poker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayController {

    @FXML private Label phaseLabel;
    @FXML private Label feedbackLabel;

    @FXML private Label p1TotalLabel;
    @FXML private Label p2TotalLabel;

    @FXML private StackPane dealerCard1, dealerCard2, dealerCard3;
    @FXML private StackPane p1Card1, p1Card2, p1Card3;
    @FXML private StackPane p2Card1, p2Card2, p2Card3;

    // I decided to throw in the action buttons and wagers which should be
    // ready for future game logic
    @FXML private Button dealButton;
    @FXML private Button p1PlayButton, p1FoldButton;
    @FXML private Button p2PlayButton, p2FoldButton;
    @FXML private TextField p1PairPlusField, p1AnteField;
    @FXML private TextField p2PairPlusField, p2AnteField;

    // the i button info
    @FXML private StackPane infoOverlay;
    @FXML private VBox infoPage1;
    @FXML private VBox infoPage2;
    @FXML private Button infoPrevBtn;
    @FXML private Button infoNextBtn;
    @FXML private Label infoPageIndicator;

    @FXML
    private void initialize() {
        //so it works when you press play
    }
    @FXML
    private void handleBackToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/uic/cs342/poker/fxml/welcome.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                getClass().getResource("/edu/uic/cs342/poker/css/style.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void handleOpenInfo() {
        showInfoPage(1);
        infoOverlay.setManaged(true);
        infoOverlay.setVisible(true);
    }

    @FXML
    private void handleCloseInfo() {
        infoOverlay.setVisible(false);
        infoOverlay.setManaged(false);
    }

    @FXML
    private void handleInfoNextPage() {
        showInfoPage(2);
    }

    @FXML
    private void handleInfoPrevPage() {
        showInfoPage(1);
    }

    private void showInfoPage(int page) {
        boolean isPage1 = (page == 1);
        infoPage1.setVisible(isPage1);
        infoPage1.setManaged(isPage1);

        infoPage2.setVisible(!isPage1);
        infoPage2.setManaged(!isPage1);

        infoPrevBtn.setVisible(!isPage1);
        infoNextBtn.setVisible(isPage1);
        infoPageIndicator.setText("Page " + page + " of 2");
    }
}