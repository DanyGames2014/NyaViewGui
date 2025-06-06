package net.danygames2014.nyaviewgui.gui;

import javax.swing.*;
import java.awt.*;

public class HelpGui extends JFrame {
    /* Main Panel */
    public JPanel mainPanel;
    public GridBagLayout mainLayout;

    public String[] helpText = {
            "Welcome to NyaView Help",
            "",
            "--- Keybinds ---",
            "F1 - Open this help menu",
            "F2 - Switch Member Tab",
            "F3 - ",
            "F4 - Toggle Tabbed Members",
            "F5 - Reload Data (Hold SHIFT for Full Reload)",
            "Alt+S - Focus Search Box (Hold SHIFT to clear the query)",
            "Alt+C - Focus Class Table",
            "Alt+M - Focus Method Table",
            "Alt+F - Focus Field Table",
            "Alt+R - Reload",
            "Alt+T - Open Translator",
            "Alt+O - Open Options",
            "Escape - Deselect currently selected elements",
            "",
            "--- Setup ---",
            "To setup the program you will need to download mappings, some of the more commonly used mappings can be found in Options -> Mappings Setup where you can download new mappings as well as manage your existing ones.",
            "For the viewer to work at all you will need atleast one set of intermediaries loaded. Either Ornithe Gen 2 or Babric are heavily recommended since they're merged.",
            "When downloading Fabric mappings based on intermediaries, you will need to also download their respective intermediaries they rely on.",
            "",
            "--- Controls ---",
            "The elements of th top bar are as follows:",
            "• Search Bar - Used to input the search query, you can also use advanced search parameters here",
            "• Search Button - If live search is not enabled you will either need to press ENTER or press this button to execute search",
            "• Search Type - Allows you to narrow the search to either class, method or field search",
            "• Mappings Type - Allows you to narrow the search to a specified type of mappings",
            "• Match Type - Allows you to toggle between the fuzzy and strict match types",
            "• Case Sensitivity - Allows you to toggle whether the search is case sensitive or not",
            "• Reload Button - Reainitialized the table model and contents. If SHIFT is held when pressing, full mapping loader reload withh be triggered",
            "• Column Filter - Allows you to hide columns which you don't want to see",
            "• Translator - Opens a stacktrace translator. Currently it can only be used on intermediary -> named",
            "• Options",
            "   • Refresh on Filter change - Controls whether the table will be reloaded when column filters are changed",
            "   • Live Search - Controls whether the search will react to search text or filters changing",
            "   • Tabbed Members - Toggles whether both methods and fields are visible at the same time or if you toggle between them",
            "   • Mappings Setup - Opens a window to manage and download mappings",
            "   • Tab Switch - If Tabbed Members are enabled, toggles tabs",
            "   • Theme - Allows you to switch themes",
            "",
            "Tip: When Tabbed members are enabled you toggle between tabs by right clicking in the member or field table",
            "",
            "--- Search ---",
            "Every aspect of searching can be controlled using the same search parameters as the command line version, these parameters take priority over those set in the graphical interface",
            "[c | m | f | a [o | oc | os | m | mc | ms | i | f | a]][? | !][!][#]<query>",
            "",
            "NOTE: Only the query is required, additional parameters are voluntary",
            "",
            " - Search Type - ",
            "[c | m | f | a] - Changing the search type allows you to only search either classes, methods or fields",
            "c - Class",
            "m - Methods",
            "f - Fields",
            "a - All",
            "",
            "- Mapping Type -",
            "[o | oc | os | m | mc | ms | i | f | a] - This options allows you to only search specific type of mappings",
            "! In order to specify the mapping type, you do also need to have search type specified !",
            "o - Obfuscated",
            "oc - Obfuscated Client",
            "os - Obfuscated Server",
            "m - MCP",
            "mc - MCP Client",
            "ms - MCP Server",
            "i - Intermediary",
            "f - Fabric",
            "a - All",
            "",
            "- Match Type -",
            "[? | !] - Prefixing the query with a question mark will make the search fuzzy, doing so with an exclamation mark will make it strict",
            "Fuzzy search means that if the searched value contains the search query, it will match it. (For example query of \"class_1\" will match \"class_1\", \"class_11\", \"class_100\" etc.",
            "Strict search means that the query has to match the searched value exactly",
            "",
            "- Child Display",
            "[!] - Adding an exclamation mark after the match type hides all members and fields of a selected class that did not match the search query",
            "This means that for example when search \"!!field_1172\" you will only see the one class it is contained in, and only see one field despite the class having more fields (and methods)",
            "",
            "- Case Sensitivity -",
            "[#] - Starting the query with a hashtag will make the search case sensitive"
    };
    
    public HelpGui() throws HeadlessException {
        super("Help");

        this.initialize();

        this.setSize(800, 600);
        this.setVisible(true);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void initialize() {
        // Main Panel
        mainLayout = new GridBagLayout();
        mainPanel = new JPanel(mainLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10,10,10,10);
        c.weightx = 1.0;
        c.weighty = 1.0;

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setBorder(null);

        StringBuilder sb = new StringBuilder();
        for (String s : helpText) {
            sb.append(s).append("\n");
        }
        textArea.setText(sb.toString());

        mainPanel.add(textScrollPane, c);

        // Add Main Panel to Frame
        this.add(mainPanel);
    }
}
