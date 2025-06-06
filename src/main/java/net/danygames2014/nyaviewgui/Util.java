package net.danygames2014.nyaviewgui;

import net.danygames2014.nyaview.ActionResult;

import javax.swing.*;
import java.awt.*;

public class Util {
    public static void showDialog(Component parent, ActionResult result) {
        String message = result.message;
        if (!result.successful()) {
            message += "\n" + "Error Code: " + result.code;
        }
        String title = result.successful() ? "Success" : "Error (Code " + result.code + ")";
        JOptionPane.showMessageDialog(parent, message, title, result.successful() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
}
