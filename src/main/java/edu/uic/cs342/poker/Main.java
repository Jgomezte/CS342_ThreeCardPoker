package edu.uic.cs342.poker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/edu/uic/cs342/poker/fxml/welcome.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 480, 280);
        scene.getStylesheets().add(
                Main.class.getResource("/edu/uic/cs342/poker/css/style.css").toExternalForm());
        stage.setTitle("CS 342 - Three Card Poker Starter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
