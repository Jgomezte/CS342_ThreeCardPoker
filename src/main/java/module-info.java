module edu.uic.cs342.poker {
    requires javafx.controls;
    requires javafx.fxml;
    exports edu.uic.cs342.poker;
    opens edu.uic.cs342.poker.controller to javafx.fxml;
}
