package edu.uic.cs342.poker;

import javafx.scene.Parent;

public final class ThemeManager {

    private static final String NIGHT_CLASS = "night";
    private static boolean night = false;

    private ThemeManager() { }

    public static boolean isNight() {
        return night;
    }

    // Flips the mode and applies it to the given scene root.
    public static void toggle(Parent root) {
        night = !night;
        apply(root);
    }

    // Call this on every new scene root so the choice carries over between screens.
    public static void apply(Parent root) {
        if (night) {
            if (!root.getStyleClass().contains(NIGHT_CLASS)) {
                root.getStyleClass().add(NIGHT_CLASS);
            }
        } else {
            root.getStyleClass().remove(NIGHT_CLASS);
        }
    }
}