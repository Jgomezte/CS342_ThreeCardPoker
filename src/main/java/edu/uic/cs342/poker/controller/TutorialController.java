package edu.uic.cs342.poker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class TutorialController {

    @FXML private Label stepCounterLabel;
    @FXML private Label tutorialNoteTitle;
    @FXML private Label tutorialNoteBody;
    @FXML private Button prevStepBtn;
    @FXML private Button nextStepBtn;

    @FXML private Button tutBackButton;
    @FXML private VBox tutP1BetBox;
    @FXML private VBox tutP2BetBox;
    @FXML private Button tutInfoButton;
    @FXML private Button tutDealButton;
    @FXML private HBox tutDealerCardsBox;
    @FXML private Button tutP1PlayBtn, tutP1FoldBtn;
    @FXML private Button tutP2PlayBtn, tutP2FoldBtn;
    @FXML private Label tutP1C1, tutP1C2, tutP1C3;
    @FXML private Label tutP2C1, tutP2C2, tutP2C3;

    @FXML private StackPane infoOverlay;
    @FXML private VBox infoPage1;
    @FXML private VBox infoPage2;
    @FXML private Button infoPrevBtn;
    @FXML private Button infoNextBtn;
    @FXML private Label infoPageIndicator;

    private int currentStep = 1;
    private static final int TOTAL_STEPS = 6;

    @FXML
    private void initialize() {
        updateTutorialStep();
    }

    @FXML
    private void handleNextStep(ActionEvent event) throws IOException {
        if (currentStep < TOTAL_STEPS) {
            currentStep++;
            updateTutorialStep();
        } else {
            switchScene(event, "/edu/uic/cs342/poker/fxml/play.fxml");
        }
    }

    @FXML
    private void handlePrevStep() {
        if (currentStep > 1) {
            currentStep--;
            updateTutorialStep();
        }
    }

    private void updateTutorialStep() {
        clearHighlights();
        stepCounterLabel.setText("TUTORIAL: STEP " + currentStep + " OF " + TOTAL_STEPS);
        prevStepBtn.setDisable(currentStep == 1);
        nextStepBtn.setText(currentStep == TOTAL_STEPS ? "Start Playing →" : "Continue →");

        switch (currentStep) {
            case 1:
                tutorialNoteTitle.setText("Step 1: Set Your Wagers");
                tutorialNoteBody.setText("First, set your Ante wager. You can do this by clicking on the arrows. Setting a pair plus wager is optional.");
                tutP1BetBox.getStyleClass().add("tutorial-highlight");
                tutP2BetBox.getStyleClass().add("tutorial-highlight");
                setSampleCards(false);
                break;

            case 2:
                tutorialNoteTitle.setText("Step 2: Deal the Cards");
                tutorialNoteBody.setText("At this point, you can still choose to change your wager, or click deal to start the round.");
                tutDealButton.getStyleClass().add("tutorial-highlight");
                setSampleCards(false);
                break;

            case 3:
                tutorialNoteTitle.setText("Step 3: Play or Fold");
                tutorialNoteBody.setText("Once the cards are dealt, you have the option to play or to fold. The player who folds will not participate in this round, but will still lose their ante and pair plus wager.");
                tutP1PlayBtn.getStyleClass().add("tutorial-highlight");
                tutP1FoldBtn.getStyleClass().add("tutorial-highlight");
                tutP2PlayBtn.getStyleClass().add("tutorial-highlight");
                tutP2FoldBtn.getStyleClass().add("tutorial-highlight");
                setSampleCards(true);
                tutDealerCardsBox.setVisible(false);
                tutDealerCardsBox.setManaged(false);
                break;

            case 4:
                tutorialNoteTitle.setText("Step 4: Dealer Cards & Round Resolution");
                tutorialNoteBody.setText("Once you play, the dealer will receive its cards, and the round will be resolved.");
                setSampleCards(true);
                tutDealerCardsBox.setVisible(true);
                tutDealerCardsBox.setManaged(true);
                break;

            case 5:
                tutorialNoteTitle.setText("Step 5: Game Rules & Information");
                tutorialNoteBody.setText("If you ever forget the rules of Three Card Poker, you can always check them out by clicking on the information button.");
                tutInfoButton.getStyleClass().add("tutorial-highlight");
                setSampleCards(false);
                break;

            case 6:
                tutorialNoteTitle.setText("Step 6: Returning to the Main Menu");
                tutorialNoteBody.setText("You can also go back to the main menu by clicking this button.");
                tutBackButton.getStyleClass().add("tutorial-highlight");
                setSampleCards(false);
                break;
        }
    }

    private void setSampleCards(boolean visible) {
        tutP1C1.setText(visible ? "Q♠" : "");
        tutP1C2.setText(visible ? "Q♥" : "");
        tutP1C3.setText(visible ? "9♦" : "");

        tutP2C1.setText(visible ? "10♣" : "");
        tutP2C2.setText(visible ? "7♦" : "");
        tutP2C3.setText(visible ? "2♠" : "");

        if (!visible) {
            tutDealerCardsBox.setVisible(false);
            tutDealerCardsBox.setManaged(false);
        }
    }

    private void clearHighlights() {
        tutBackButton.getStyleClass().remove("tutorial-highlight");
        tutP1BetBox.getStyleClass().remove("tutorial-highlight");
        tutP2BetBox.getStyleClass().remove("tutorial-highlight");
        tutInfoButton.getStyleClass().remove("tutorial-highlight");
        tutDealButton.getStyleClass().remove("tutorial-highlight");
        tutP1PlayBtn.getStyleClass().remove("tutorial-highlight");
        tutP1FoldBtn.getStyleClass().remove("tutorial-highlight");
        tutP2PlayBtn.getStyleClass().remove("tutorial-highlight");
        tutP2FoldBtn.getStyleClass().remove("tutorial-highlight");
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) throws IOException {
        switchScene(event, "/edu/uic/cs342/poker/fxml/welcome.fxml");
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