package net.danygames2014.nyaviewgui;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaviewgui.gui.MappingGui;

import javax.swing.*;

public class NyaViewGui {
    public static MappingGui mappingGui;
    public static GuiConfig guiConfig;

    public static void main(String[] args) {
        NyaView.init();

        guiConfig = new GuiConfig(NyaView.config);

        try {
            UIManager.setLookAndFeel(guiConfig.getTheme());
        } catch (UnsupportedLookAndFeelException ignored) {
        }

        mappingGui = new MappingGui();
    }
}
