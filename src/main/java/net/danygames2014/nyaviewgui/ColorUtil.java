package net.danygames2014.nyaviewgui;

import javax.swing.*;
import java.awt.*;

public class ColorUtil {
    public static Color getAlternatePanelColor() {
        Color panelColor = UIManager.getColor("Panel.background");

        if (panelColor == null) {
            panelColor = Color.WHITE;
        }

        // Calculate average brightness
        int brightness = (panelColor.getRed() + panelColor.getGreen() + panelColor.getBlue()) / 3;

        float factor = brightness < 128 ? 1.15f : 0.95f;

        int r = clampToByte((int) (panelColor.getRed() * factor));
        int g = clampToByte((int) (panelColor.getGreen() * factor));
        int b = clampToByte((int) (panelColor.getBlue() * factor));

        return new Color(r, g, b);
    }

    public static int clampToByte(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
