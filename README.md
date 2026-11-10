# CS 342 Three Card Poker — Minimal Maven Starter

This starter deliberately contains **one screen only**, with a single label and button.
The label initially reads **welcome to project 1**. Clicking **Start** invokes
`WelcomeController.handleStartButton()` and changes it to **get started**.

## Requirements
- JDK 25
- Maven 3.9+ (or compatible)

## Run
```bash
mvn clean javafx:run
```

## Test
```bash
mvn test
```

## Where to add your work
- `src/main/java/`: Java application, controllers, model, and game logic
- `src/main/resources/edu/uic/cs342/poker/fxml/`: FXML layouts
- `src/main/resources/edu/uic/cs342/poker/css/`: CSS stylesheets
- `src/main/resources/edu/uic/cs342/poker/images/`: images and other assets
- `src/test/java/`: JUnit tests

The starter does **not** implement poker rules, betting, additional screens,
animations, or final UI styling. These are student responsibilities.
