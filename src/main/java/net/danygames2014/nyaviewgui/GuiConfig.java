package net.danygames2014.nyaviewgui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import net.danygames2014.nyaview.Config;
import net.danygames2014.nyaview.NyaView;
import org.simpleyaml.configuration.file.YamlFile;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.util.HashMap;

public class GuiConfig {
    public Config config;
    public HashMap<String, LookAndFeel> themes;
    public YamlFile configFile;

    public GuiConfig(Config config) {
        themes = new HashMap<>();
        themes.put("metal", new MetalLookAndFeel());
        themes.put("nimbus", new NimbusLookAndFeel());
        themes.put("flatlaf", new FlatLightLaf());
        themes.put("flatlafdark", new FlatDarkLaf());
        themes.put("flatlafmac", new FlatMacLightLaf());
        themes.put("flatlafmacdark", new FlatMacDarkLaf());
        themes.put("flatlafintellij", new FlatIntelliJLaf());
        themes.put("flatlafdarcula", new FlatDarculaLaf());

        this.config = config;
        configFile = config.getYamlFile();
        configFile.addDefault("theme", "flatlaf");
        configFile.addDefault("refreshOnFilterChange", true);
        configFile.addDefault("liveSearch", true);
        configFile.addDefault("tabbedMembers", false);
        config.save();
    }

    public boolean getOption(String key) {
        if (configFile.contains(key)) {
            return configFile.getBoolean(key);
        }
        NyaView.LOGGER.warn("Tried to get a non-existent option from config " + key);
        return false;
    }

    public void setOption(String key, boolean value) {
        configFile.set(key, value);
        config.save();
    }

    public LookAndFeel getTheme() {
        return themes.getOrDefault(configFile.getString("theme"), new FlatLightLaf());
    }

    public void setTheme(LookAndFeel theme) {
        for (var themeEntry : themes.entrySet()) {
            if (themeEntry.getValue().equals(theme)) {
                configFile.set("theme", themeEntry.getKey());
            }
        }
        config.save();
    }
}
