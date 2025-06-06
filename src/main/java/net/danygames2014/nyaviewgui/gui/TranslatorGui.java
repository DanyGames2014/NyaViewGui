package net.danygames2014.nyaviewgui.gui;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;
import net.danygames2014.nyaview.search.DisplayParameters;
import net.danygames2014.nyaview.search.Search;
import net.danygames2014.nyaview.search.SearchParameters;
import net.danygames2014.nyaview.search.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("DuplicatedCode")
public class TranslatorGui extends JFrame {
    /* Main Layout */
    public JPanel mainPanel;
    public GridBagLayout mainLayout;

    /* Translation */
    public JTextArea inputTextArea;
    public JPanel translatePanel;
    public JButton translateButton;
    public JCheckBox fullPackagePath;
    public JComboBox<Mappings> mappingsSelector;
    public JTextArea outputTextArea;

    public TranslatorGui() throws HeadlessException {
        super("Translator");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initialize();

        this.setSize(1280, 720);
        this.setVisible(true);
    }

    public void initialize() {
        // Main Panel
        mainLayout = new GridBagLayout();
        mainPanel = new JPanel(mainLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(3, 3, 3, 3);
        c.gridx = 0;
        c.weightx = 1.0;

        // Input
        inputTextArea = new JTextArea();

        c.gridy = 0;
        c.weighty = 0.48;
        mainPanel.add(new JScrollPane(inputTextArea), c);

        // Translate Panel
        translatePanel = new JPanel();

        // Translate Button
        translateButton = new JButton("Translate");
        translateButton.addActionListener(e -> {
            translate();
        });
        translatePanel.add(translateButton);

        // Mappings Selector
        mappingsSelector = new JComboBox<>();
        for (var item : NyaView.loader.mappings.entrySet()) {
            mappingsSelector.addItem(item.getValue());
        }
        translatePanel.add(mappingsSelector);

        // Full Package checkbox
        fullPackagePath = new JCheckBox("Full Package");
        translatePanel.add(fullPackagePath);

        // Add Translate Panel
        c.fill = GridBagConstraints.NONE;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.04;
        mainPanel.add(translatePanel, c);

        // Output
        outputTextArea = new JTextArea();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 0.48;
        mainPanel.add(new JScrollPane(outputTextArea), c);

        // Add to Main Panel
        this.add(mainPanel);
    }

    public void translate() {
        String inputText = inputTextArea.getText();
        StringBuilder outputText = new StringBuilder();

        Pattern intermediaryPattern = Pattern.compile("\\b(class|method|field)_(\\d+)\\b");
        Pattern obfuscatedPattern = Pattern.compile("\\b[a-z]{1,3}\\b");

        for (String line : inputText.split("\n")) {
            // Cleanup the line
            line = line.replaceAll("net.minecraft.", "");
            line = line.replaceAll("knot//", "");

            Matcher matcher = intermediaryPattern.matcher(line);

            StringBuilder result = new StringBuilder();

            while (matcher.find()) {
                String replacement = translateIntermediary(matcher.group());
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }

            matcher.appendTail(result);

            outputText.append(result).append("\n");
        }

        outputTextArea.setText(outputText.toString());
    }

    public String translateIntermediary(String member) {
        try {
            SearchParameters.SearchType type;
            if (member.contains("field")) {
                type = SearchParameters.SearchType.FIELD;
            } else if (member.contains("method")) {
                type = SearchParameters.SearchType.METHOD;
            } else {
                type = SearchParameters.SearchType.CLASS;
            }

            SearchParameters parameters = new SearchParameters();
            parameters.classDisplay = DisplayParameters.ClassDisplay.MINIMAL;
            parameters.type = type;
            parameters.match = SearchParameters.MatchType.STRICT;
            parameters.query = member;
            parameters.mappings = SearchParameters.SearchMappings.ALL;

            SearchResult result = Search.search(parameters);
            Mappings selectedMappings = ((Mappings) mappingsSelector.getSelectedItem());
            if (selectedMappings == null) {
                return member;
            }

            for (var r : result.results.entrySet()) {
                ClassMappingEntry c = r.getKey();
                SearchResult.SearchResultClassEntry s = r.getValue();

                switch (type) {
                    case CLASS -> {
                        switch (selectedMappings.type) {
                            case MCP -> {
                                if (fullPackagePath.isSelected()) {
                                    return c.mcp.get(selectedMappings).getFullPath().replaceAll("/", ".");
                                } else {
                                    return c.mcp.get(selectedMappings).name;
                                }
                            }
                            case BABRIC -> {
                                if (fullPackagePath.isSelected()) {
                                    return c.babric.get(selectedMappings).getFullPath().replaceAll("/", ".");
                                } else {
                                    return c.babric.get(selectedMappings).name;
                                }
                            }
                        }
                    }
                    case METHOD -> {
                        switch (selectedMappings.type) {
                            case MCP -> {
                                if (!s.methods.isEmpty()) {
                                    return r.getValue().methods.get(0).mcp.get(selectedMappings).name;
                                }
                            }
                            case BABRIC -> {
                                if (!s.methods.isEmpty()) {
                                    return r.getValue().methods.get(0).babric.get(selectedMappings).name;
                                }
                            }
                        }
                    }
                    case FIELD -> {
                        switch (selectedMappings.type) {
                            case MCP -> {
                                if (!s.fields.isEmpty()) {
                                    return r.getValue().fields.get(0).mcp.get(selectedMappings);
                                }
                            }
                            case BABRIC -> {
                                if (!s.fields.isEmpty()) {
                                    return r.getValue().fields.get(0).babric.get(selectedMappings);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //NyaView.LOGGER.error("Error while translating", e);
            return member;
        }

        return member;
    }

    public String translateObfuscated(String member) {
        return "#" + member + "#";
    }
}
