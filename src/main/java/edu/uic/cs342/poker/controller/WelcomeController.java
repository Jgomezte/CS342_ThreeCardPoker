package edu.uic.cs342.poker.controller;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private Label titleLabel;

    @FXML private StackPane infoOverlay;
    @FXML private VBox infoPage1;
    @FXML private VBox infoPage2;
    @FXML private Button infoPrevBtn;
    @FXML private Button infoNextBtn;
    @FXML private Label infoPageIndicator;

    @FXML
    private void initialize() {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.5), titleLabel);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.08);
        pulse.setToY(1.08);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
    }

    @FXML
    private void handleQuitButton() {
        Platform.exit();
    }

    @FXML
    private void handlePlayButton(ActionEvent event) throws IOException {
        switchScene(event, "/edu/uic/cs342/poker/fxml/play.fxml");
    }

    @FXML
    private void handleTutorialButton(ActionEvent event) throws IOException {
        switchScene(event, "/edu/uic/cs342/poker/fxml/tutorial.fxml");
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

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                getClass().getResource("/edu/uic/cs342/poker/css/style.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}