package net.danygames2014.nyaviewgui.gui;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;
import net.danygames2014.nyaview.mapping.entry.FieldMappingEntry;
import net.danygames2014.nyaview.mapping.entry.MethodMappingEntry;
import net.danygames2014.nyaview.search.DisplayParameters.ClassDisplay;
import net.danygames2014.nyaview.search.Search;
import net.danygames2014.nyaview.search.SearchParameters;
import net.danygames2014.nyaview.search.SearchResult;
import net.danygames2014.nyaviewgui.NyaViewGui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

@SuppressWarnings({"DuplicatedCode"})
public class MappingGui extends JFrame {
    /* Main Layout */
    public JPanel mainPanel;
    public BorderLayout mainLayout;

    /* Toolbar */
    public JPanel toolbarPanel;
    public GridBagLayout toolbarLayout;

    // Left Toolbar
    public JPanel leftToolbarPanel;
    public FlowLayout leftToolbarLayout;

    public JTextField searchField;
    public JButton searchButton;
    public JComboBox<SearchParameters.SearchType> searchComboBox;
    public JComboBox<SearchParameters.SearchMappings> mappingComboBox;
    public JComboBox<SearchParameters.MatchType> matchComboBox;
    public JCheckBox caseSensitive;

    // Right Toolbar
    public JPanel rightToolbarPanel;
    public FlowLayout rightToolbarLayout;

    public JButton reloadButton;
    public JButton columnFilterButton;
    public JPopupMenu columnFilterPopup;
    public JButton optionsButton;
    public JPopupMenu optionsPopup;
    public JButton translatorButton;

    /* Main Split Pane */
    // Split panes
    public JSplitPane mainSplitPane;

    public CardLayout memberLayout;
    public JPanel memberPanel;
    public JSplitPane methodFieldSplitPane;

    // Class Table
    public JTable classTable;
    public ClassTableModel classTableModel;

    // Method Table
    public JTable methodTable;
    public JScrollPane methodScrollPane;
    public DefaultTableModel methodTableModel;

    // Field Table
    public JTable fieldTable;
    public JScrollPane fieldScrollPane;
    public DefaultTableModel fieldTableModel;

    /* Menu */
    public JMenu menu;
    public JMenuBar menuBar;
    public JCheckBoxMenuItem refreshOnFilterMenuItem;
    public JCheckBoxMenuItem liveSearchMenuItem;
    public JCheckBoxMenuItem tabbedMembers;
    public JCheckBoxMenuItem horizontalScrollbars;
    public JCheckBoxMenuItem compactObfuscatedColumns;

    /* Help */
    HelpGui helpGui;

    /* Search */
    public SearchResult currentSearch = null;
    public SearchParameters currentSearchParameters = null;

    public MappingGui() throws HeadlessException {
        super("NyaView");
        updateWindowTitle();
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initialize();
        initKeyboardManager();

        this.setSize(1280, 720);
        this.setVisible(true);

        initColumnFilters();
        search("");
    }

    public void updateWindowTitle() {
        this.setTitle("NyaView (" + NyaView.profileManager.activeProfile.getName() + ")");
    }

    public void initKeyboardManager() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() != KeyEvent.KEY_PRESSED) {
                    return false;
                }

                switch (e.getKeyCode()) {
                    // F1 - Open Help
                    case KeyEvent.VK_F1 -> {
                        if (helpGui == null || !helpGui.isDisplayable()) {
                            helpGui = new HelpGui();
                        }
                        return true;
                    }

                    // F2 - Switch Member Tabs
                    case KeyEvent.VK_F2 -> {
                        memberLayout.next(memberPanel);
                        return true;
                    }

                    // F3 -
                    case KeyEvent.VK_F3 -> {

                    }

                    // F4 - Toggle Tabbed Members
                    case KeyEvent.VK_F4 -> {
                        tabbedMembers.setSelected(!tabbedMembers.isSelected());
                        NyaViewGui.guiConfig.setOption("tabbedMembers", tabbedMembers.isSelected());
                        swapMemberLayer(tabbedMembers.isSelected());
                        return true;
                    }

                    // F5 - Reload (+SHIFT for full Reload)
                    case KeyEvent.VK_F5 -> {
                        reloadData(e.isShiftDown());
                        return true;
                    }

                    // Alt+S - Focus Search Field (+SHIFT to clear query)
                    case KeyEvent.VK_S -> {
                        if (e.isAltDown()) {
                            if (e.isShiftDown()) {
                                searchField.setText("");
                            }
                            searchField.grabFocus();
                            return true;
                        }
                    }

                    // Alt+C - Focus Class Table
                    case KeyEvent.VK_C -> {
                        if (e.isAltDown()) {
                            classTable.grabFocus();
                            return true;
                        }
                    }

                    // Alt+F - Focus Field Table
                    case KeyEvent.VK_F -> {
                        if (e.isAltDown()) {
                            fieldTable.grabFocus();
                            return true;
                        }
                    }

                    // Alt+M - Focus Method Table
                    case KeyEvent.VK_M -> {
                        if (e.isAltDown()) {
                            methodTable.grabFocus();
                            return true;
                        }
                    }

                    case KeyEvent.VK_ESCAPE -> {
                        mainPanel.grabFocus();
                        classTable.clearSelection();
                        methodTable.clearSelection();
                        fieldTable.clearSelection();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void initialize() {
        // Main Panel
        mainLayout = new BorderLayout();
        mainPanel = new JPanel(mainLayout);

        // Toolbar Panel
        toolbarLayout = new GridBagLayout();
        leftToolbarLayout = new FlowLayout(FlowLayout.LEFT);
        rightToolbarLayout = new FlowLayout(FlowLayout.RIGHT);

        toolbarPanel = new JPanel(toolbarLayout);

        // Left Toolbar
        leftToolbarPanel = new JPanel(leftToolbarLayout);

        leftToolbarPanel.add(new JLabel("Search "));

        searchField = new JTextField("", 40);
        searchField.addActionListener(e ->
                search(searchField.getText())
        );
        searchField.addCaretListener(e -> {
            if (liveSearchMenuItem.isSelected()) {
                search(searchField.getText());
            }
        });
        leftToolbarPanel.add(searchField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(e ->
                search(searchField.getText())
        );
        leftToolbarPanel.add(searchButton);

        searchComboBox = new JComboBox<>();
        searchComboBox.setModel(new DefaultComboBoxModel<>(SearchParameters.SearchType.values()));
        searchComboBox.setSelectedIndex(Search.defaultType.ordinal());
        searchComboBox.addActionListener(e -> {
            Search.defaultType = SearchParameters.SearchType.values()[searchComboBox.getSelectedIndex()];
            if (liveSearchMenuItem.isSelected()) {
                search(searchField.getText());
            }
        });
        leftToolbarPanel.add(searchComboBox);

        mappingComboBox = new JComboBox<>();
        mappingComboBox.setModel(new DefaultComboBoxModel<>(SearchParameters.SearchMappings.values()));
        mappingComboBox.setSelectedIndex(Search.defaultMappings.ordinal());
        mappingComboBox.addActionListener(e -> {
            Search.defaultMappings = SearchParameters.SearchMappings.values()[mappingComboBox.getSelectedIndex()];
            if (liveSearchMenuItem.isSelected()) {
                search(searchField.getText());
            }
        });
        leftToolbarPanel.add(mappingComboBox);

        matchComboBox = new JComboBox<>();
        matchComboBox.setModel(new DefaultComboBoxModel<>(SearchParameters.MatchType.values()));
        matchComboBox.setSelectedIndex(Search.defaultMatch.ordinal());
        matchComboBox.addActionListener(e -> {
            Search.defaultMatch = SearchParameters.MatchType.values()[matchComboBox.getSelectedIndex()];
            if (liveSearchMenuItem.isSelected()) {
                search(searchField.getText());
            }
        });
        leftToolbarPanel.add(matchComboBox);

        caseSensitive = new JCheckBox("Case Sensitive");
        caseSensitive.addActionListener(e -> {
            Search.defaultCaseSensitive = caseSensitive.isSelected();
            if (liveSearchMenuItem.isSelected()) {
                search(searchField.getText());
            }
        });
        leftToolbarPanel.add(caseSensitive);

        GridBagConstraints leftToolbarConstraint = new GridBagConstraints();
        leftToolbarConstraint.fill = GridBagConstraints.VERTICAL;
        leftToolbarConstraint.gridx = 0;
        leftToolbarConstraint.gridy = 0;
        leftToolbarConstraint.weightx = 0.5;
        leftToolbarConstraint.anchor = GridBagConstraints.LINE_START;

        toolbarPanel.add(leftToolbarPanel, leftToolbarConstraint);

        // Right Toolbar
        rightToolbarPanel = new JPanel(rightToolbarLayout);

        // Reload Button
        reloadButton = new JButton("Reload");
        reloadButton.addActionListener(e -> {
            reloadData((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
        });
        reloadButton.setMnemonic(KeyEvent.VK_R);
        rightToolbarPanel.add(reloadButton);

        // Column Filter
        columnFilterButton = new JButton("Column Filter");
        columnFilterButton.addActionListener(e -> {
            columnFilterPopup.show(columnFilterButton, 0, columnFilterButton.getHeight());
        });
        columnFilterPopup = new JPopupMenu();
        rightToolbarPanel.add(columnFilterButton);

        // Translator
        translatorButton = new JButton("Translator");
        translatorButton.addActionListener(e -> {
            TranslatorGui translatorGui = new TranslatorGui();
        });
        translatorButton.setMnemonic(KeyEvent.VK_T);
        rightToolbarPanel.add(translatorButton);

        // Options
        optionsButton = new JButton("Options");
        optionsButton.addActionListener(e -> {
            optionsPopup.show(optionsButton, 0, optionsButton.getHeight());
        });
        optionsButton.setMnemonic(KeyEvent.VK_O);
        optionsPopup = new JPopupMenu();

        refreshOnFilterMenuItem = new JCheckBoxMenuItem("Refresh On Filter Change", NyaViewGui.guiConfig.getOption("refreshOnFilterChange"));
        refreshOnFilterMenuItem.addActionListener(e -> {
            NyaViewGui.guiConfig.setOption("refreshOnFilterChange", refreshOnFilterMenuItem.isSelected());
        });
        optionsPopup.add(refreshOnFilterMenuItem);

        liveSearchMenuItem = new JCheckBoxMenuItem("Live Search", NyaViewGui.guiConfig.getOption("liveSearch"));
        liveSearchMenuItem.addActionListener(e -> {
            NyaViewGui.guiConfig.setOption("liveSearch", liveSearchMenuItem.isSelected());
        });
        optionsPopup.add(liveSearchMenuItem);

        tabbedMembers = new JCheckBoxMenuItem("Tabbed Members", NyaViewGui.guiConfig.getOption("tabbedMembers"));
        tabbedMembers.addActionListener(e -> {
            NyaViewGui.guiConfig.setOption("tabbedMembers", tabbedMembers.isSelected());
            swapMemberLayer(tabbedMembers.isSelected());
        });
        optionsPopup.add(tabbedMembers);

        horizontalScrollbars = new JCheckBoxMenuItem("Horizontal Scrollbars", NyaViewGui.guiConfig.getOption("horizontalScrollbars"));
        horizontalScrollbars.addActionListener(e -> {
            NyaViewGui.guiConfig.setOption("horizontalScrollbars", horizontalScrollbars.isSelected());
            adjustColumnWidths();
        });
        optionsPopup.add(horizontalScrollbars);

        compactObfuscatedColumns = new JCheckBoxMenuItem("Compact Obfuscated Columns", NyaViewGui.guiConfig.getOption("compactObfuscatedColumns"));
        compactObfuscatedColumns.addActionListener(e -> {
            NyaViewGui.guiConfig.setOption("compactObfuscatedColumns", compactObfuscatedColumns.isSelected());
            adjustColumnWidths();
        });
        optionsPopup.add(compactObfuscatedColumns);


        JMenuItem optionsMenuItem = new JMenuItem("Mappings Setup");
        optionsMenuItem.addActionListener(e -> {
            SetupGui setupGui = new SetupGui(this);
        });
        optionsPopup.add(optionsMenuItem);

        JMenuItem tabSwitch = new JMenuItem("Tab Switch");
        tabSwitch.addActionListener(e -> {
            memberLayout.next(memberPanel);
        });
        optionsPopup.add(tabSwitch);
        rightToolbarPanel.add(optionsButton);

        JMenu themes = new JMenu("Theme");
        for (var theme : NyaViewGui.guiConfig.themes.values()) {
            themes.add(themeButton(theme));
        }
        optionsPopup.add(themes);

        JMenuItem helpMenuitem = new JMenuItem("Help");
        helpMenuitem.addActionListener(e -> {
            if (helpGui == null || !helpGui.isDisplayable()) {
                helpGui = new HelpGui();
            }
        });
        optionsPopup.add(helpMenuitem);

        GridBagConstraints rightToolbarConstraint = new GridBagConstraints();
        rightToolbarConstraint.fill = GridBagConstraints.VERTICAL;
        rightToolbarConstraint.gridx = 1;
        rightToolbarConstraint.gridy = 0;
        rightToolbarConstraint.weightx = 0.5;
        rightToolbarConstraint.anchor = GridBagConstraints.LINE_END;

        toolbarPanel.add(rightToolbarPanel, rightToolbarConstraint);

        // Add Toolbar Panel to Main Panel
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Main Split Pane
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerSize(5);

        // Create Table for Class Mapping Entries
        classTable = new JTable(classTableModel);
        classTable.setAutoCreateRowSorter(true);
        classTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        classTable.setRowSelectionAllowed(true);
        classTable.setColumnSelectionAllowed(false);
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.getTableHeader().setReorderingAllowed(false);
        classTable.setShowGrid(true);
        classTable.setDefaultEditor(Object.class, null);
        classTable.getSelectionModel().addListSelectionListener(new ClassTableListener(this, classTable));

        JScrollPane classScrollPane = new JScrollPane(classTable);
        mainSplitPane.add(classScrollPane);

        // Tabbed
        memberLayout = new CardLayout();
        memberPanel = new JPanel(memberLayout);

        // Not Tabbed
        methodFieldSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        methodFieldSplitPane.setDividerSize(5);

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    memberLayout.next(memberPanel);
                }
            }
        };

        methodTable = new JTable(methodTableModel);
        methodTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        methodTable.setAutoCreateRowSorter(true);
        methodTable.setRowSelectionAllowed(true);
        methodTable.setColumnSelectionAllowed(false);
        methodTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        methodTable.getTableHeader().setReorderingAllowed(false);
        methodTable.setShowGrid(true);
        methodTable.setDefaultEditor(Object.class, null);
        methodTable.addMouseListener(mouseListener);
        methodScrollPane = new JScrollPane(methodTable);

        fieldTable = new JTable(fieldTableModel);
        fieldTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        fieldTable.setAutoCreateRowSorter(true);
        fieldTable.setRowSelectionAllowed(true);
        fieldTable.setColumnSelectionAllowed(false);
        fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldTable.getTableHeader().setReorderingAllowed(false);
        fieldTable.setShowGrid(true);
        fieldTable.setDefaultEditor(Object.class, null);
        fieldTable.addMouseListener(mouseListener);
        fieldScrollPane = new JScrollPane(fieldTable);

        swapMemberLayer(tabbedMembers.isSelected());

        mainSplitPane.setResizeWeight(0.6d);
        methodFieldSplitPane.setResizeWeight(0.4d);

        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

        // Add Main Panel to JFrame
        this.add(mainPanel);
    }

    public void reloadData(boolean fullReload) {
        if (fullReload) {
            NyaView.init();
            initColumnFilters();
            initTableModels();
            refreshTableContents();
            search("");
        } else {
            initTableModels();
            refreshTableContents();
        }
    }

    public JMenuItem themeButton(LookAndFeel lookAndFeel) {
        JMenuItem menuItem = new JMenuItem(lookAndFeel.getName());
        menuItem.addActionListener(e -> {
            try {
                NyaViewGui.guiConfig.setTheme(lookAndFeel);
                UIManager.setLookAndFeel(lookAndFeel);
                updateTheme();
                updateTheme();
                classTable.setShowGrid(true);
                methodTable.setShowGrid(true);
                fieldTable.setShowGrid(true);
            } catch (UnsupportedLookAndFeelException ignored) {

            }
        });
        return menuItem;
    }

    public void updateTheme() {
        SwingUtilities.updateComponentTreeUI(this);
        SwingUtilities.updateComponentTreeUI(columnFilterPopup);
        SwingUtilities.updateComponentTreeUI(optionsPopup);
    }

    public void swapMemberLayer(boolean tabbed) {
        if (tabbed) {
            mainSplitPane.remove(methodFieldSplitPane);
            memberPanel.removeAll();

            methodScrollPane = new JScrollPane(methodTable);
            memberPanel.add(methodScrollPane, "methods");

            fieldScrollPane = new JScrollPane(fieldTable);
            memberPanel.add(fieldScrollPane, "fields");

            mainSplitPane.add(memberPanel);
        } else {
            mainSplitPane.remove(memberPanel);
            methodFieldSplitPane.removeAll();

            methodScrollPane = new JScrollPane(methodTable);
            methodFieldSplitPane.add(methodScrollPane);

            fieldScrollPane = new JScrollPane(fieldTable);
            methodFieldSplitPane.add(fieldScrollPane);

            methodFieldSplitPane.setResizeWeight(0.4d);
            mainSplitPane.add(methodFieldSplitPane);
        }

        methodScrollPane.updateUI();
        fieldScrollPane.updateUI();
        methodFieldSplitPane.updateUI();
        memberPanel.updateUI();
    }

    public void search(String query) {
        currentSearchParameters = SearchParameters.parse(query);
        currentSearch = Search.search(currentSearchParameters);

        initTableModels();
        refreshTableContents();
    }

    public void initColumnFilters() {
        columnFilterPopup.removeAll();
        ColumnHelper.clear();

        addColumnFilterCheckbox("Environment", "environment");
        columnFilterPopup.addSeparator();

        // MCP Mappings
        for (var mapping : NyaView.loader.mappings.values()) {
            if (mapping.type == MappingType.MCP) {
                addColumnFilterCheckbox(mapping.name, "mcp/" + mapping.id);
            }
        }

        columnFilterPopup.addSeparator();
        addColumnFilterCheckbox("Obfuscated Client", "obfuscatedClient");
        addColumnFilterCheckbox("Obfuscated Server", "obfuscatedServer");

        // Intermediaries
        columnFilterPopup.addSeparator();
        for (var mapping : NyaView.loader.intermediaries.values()) {
            addColumnFilterCheckbox(mapping.name, "intermediary/" + mapping.id);
        }

        // Fabric Mappings
        columnFilterPopup.addSeparator();
        for (var mapping : NyaView.loader.mappings.values()) {
            if (mapping.type == MappingType.BABRIC) {
                addColumnFilterCheckbox(mapping.name, "fabric/" + mapping.id);
            }
        }
    }

    public void addColumnFilterCheckbox(String text, String key) {
        JCheckBoxMenuItem checkbox = new JCheckBoxMenuItem(text, true);

        checkbox.addItemListener(e -> checkboxListener(e, key, refreshOnFilterMenuItem.isSelected()));

        columnFilterPopup.add(checkbox);
        ColumnHelper.add(key);
    }

    private static void checkboxListener(ItemEvent e, String key, boolean refresh) {
        if (e.getItem() instanceof JCheckBoxMenuItem checkbox) {
            if (checkbox.isSelected()) {
                ColumnHelper.allow(key);
            } else {
                ColumnHelper.hide(key);
            }

            if (refresh) {
                NyaViewGui.mappingGui.initTableModels();
                NyaViewGui.mappingGui.refreshTableContents();
            }
        }
    }

    public void refreshTableContents() {
        if (currentSearch == null) {
            return;
        }

        // Add Rows
        for (var item : currentSearch.results.entrySet()) {

            ClassMappingEntry classEntry = item.getKey();
            ArrayList<String> row = new ArrayList<>();

            // Environment
            if (ColumnHelper.isAllowed("environment")) {
                row.add(classEntry.environment.toString());
            }

            // MCP
            for (Mappings mapping : NyaView.loader.mappings.values()) {
                if (mapping.type == MappingType.MCP) {
                    if (ColumnHelper.isAllowed("mcp/" + mapping.id)) {
                        if (classEntry.mcp.containsKey(mapping)) {
                            row.add(classEntry.mcp.get(mapping).name);
                        } else {
                            row.add("");
                        }
                    }
                }
            }

            // Obfuscated Client
            if (ColumnHelper.isAllowed("obfuscatedClient")) {
                row.add(classEntry.obfuscatedClient);
            }

            // Obfuscated Server
            if (ColumnHelper.isAllowed("obfuscatedServer")) {
                row.add(classEntry.obfuscatedServer);
            }

            // Intermediary
            for (var intermediary : NyaView.loader.intermediaries.values()) {
                if (ColumnHelper.isAllowed("intermediary/" + intermediary.id)) {
                    if (classEntry.intermediary.containsKey(intermediary)) {
                        row.add(classEntry.intermediary.get(intermediary).name);
                    } else {
                        row.add("");
                    }
                }
            }

            // Babric
            for (Mappings mapping : NyaView.loader.mappings.values()) {
                if (mapping.type == MappingType.BABRIC) {
                    if (ColumnHelper.isAllowed("fabric/" + mapping.id)) {
                        if (classEntry.babric.containsKey(mapping)) {
                            row.add(classEntry.babric.get(mapping).name);
                        } else {
                            row.add("");
                        }
                    }
                }
            }

            classTableModel.addRow(row.toArray(), classEntry);
        }
    }

    public void initTableModels() {
        /// Model Table
        NyaView.LOGGER.debug("Refreshing Model Table");
        methodTableModel = initTableModel(false);

        methodTable.setModel(methodTableModel);

        /// Field Table
        NyaView.LOGGER.debug("Refreshing Field Table");
        fieldTableModel = initTableModel(true);

        fieldTable.setModel(fieldTableModel);

        /// Class Table
        NyaView.LOGGER.debug("Refreshing Class Table");

        classTableModel = initClasTableModel();

        classTable.setModel(classTableModel);

        adjustColumnWidths();
    }

    public ClassTableModel initClasTableModel() {
        ClassTableModel tableModel = new ClassTableModel();

        if (ColumnHelper.isAllowed("environment")) {
            tableModel.addColumn("Environment");
        }

        // MCP Mappings
        for (var mapping : NyaView.loader.mappings.values()) {
            if (mapping.type == MappingType.MCP) {
                if (ColumnHelper.isAllowed("mcp/" + mapping.id)) {
                    tableModel.addColumn(mapping.name);
                }
            }
        }

        if (ColumnHelper.isAllowed("obfuscatedClient")) {
            tableModel.addColumn("Obfuscated Client");
        }
        if (ColumnHelper.isAllowed("obfuscatedServer")) {
            tableModel.addColumn("Obfuscated Server");
        }

        // Intermediaries
        for (var mapping : NyaView.loader.intermediaries.values()) {
            if (ColumnHelper.isAllowed("intermediary/" + mapping.id)) {
                tableModel.addColumn(mapping.name);
            }
        }

        // Fabric Mappings
        for (var mapping : NyaView.loader.mappings.values()) {
            if (mapping.type == MappingType.BABRIC) {
                if (ColumnHelper.isAllowed("fabric/" + mapping.id)) {
                    tableModel.addColumn(mapping.name);
                }
            }
        }

        return tableModel;
    }

    public DefaultTableModel initTableModel(boolean noEnvironment) {
        DefaultTableModel tableModel = new DefaultTableModel();

        if (ColumnHelper.isAllowed("environment")) {
            if (!noEnvironment) {
                tableModel.addColumn("Environment");
            }
        }

        // MCP Mappings
        for (var mapping : NyaView.loader.mappings.values()) {
            if (mapping.type == MappingType.MCP) {
                if (ColumnHelper.isAllowed("mcp/" + mapping.id)) {
                    tableModel.addColumn(mapping.name);
                }
            }
        }

        if (ColumnHelper.isAllowed("obfuscatedClient")) {
            tableModel.addColumn("Obfuscated Client");
        }
        if (ColumnHelper.isAllowed("obfuscatedServer")) {
            tableModel.addColumn("Obfuscated Server");
        }

        // Intermediaries
        for (var mapping : NyaView.loader.intermediaries.values()) {
            if (ColumnHelper.isAllowed("intermediary/" + mapping.id)) {
                tableModel.addColumn(mapping.name);
            }
        }

        // Fabric Mappings
        for (var mapping : NyaView.loader.mappings.values()) {
            if (mapping.type == MappingType.BABRIC) {
                if (ColumnHelper.isAllowed("fabric/" + mapping.id)) {
                    tableModel.addColumn(mapping.name);
                }
            }
        }

        return tableModel;
    }

    public void adjustColumnWidths() {
        classTable.setAutoResizeMode(horizontalScrollbars.isSelected() ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS);
        SwingUtilities.invokeLater(() -> {
            ColumnHelper.adjustColumns(classTable);
        });

        methodTable.setAutoResizeMode(horizontalScrollbars.isSelected() ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS);
        SwingUtilities.invokeLater(() -> {
            ColumnHelper.adjustColumns(methodTable);
        });


        fieldTable.setAutoResizeMode(horizontalScrollbars.isSelected() ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS);
        SwingUtilities.invokeLater(() -> {
            ColumnHelper.adjustColumns(fieldTable);
        });
    }

    static class ClassTableListener implements ListSelectionListener {
        private final JTable table;
        private final MappingGui gui;

        public ClassTableListener(MappingGui mappingGui, JTable table) {
            this.table = table;
            this.gui = mappingGui;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row > -1) {
                    if (this.table.getModel() instanceof ClassTableModel classTable) {
                        ClassMappingEntry c = classTable.getClassMappingEntry(row);

                        // Methods
                        DefaultTableModel methodTable = gui.initTableModel(false);

                        ArrayList<MethodMappingEntry> methods;
                        if (gui.currentSearchParameters.classDisplay == ClassDisplay.NONE || gui.currentSearchParameters.classDisplay == ClassDisplay.MINIMAL) {
                            methods = gui.currentSearch.results.get(c).methods;
                        } else {
                            methods = c.methods;
                        }

                        for (var m : methods) {
                            ArrayList<String> r = new ArrayList<>();

                            if (ColumnHelper.isAllowed("environment")) {
                                r.add(m.environment.toString());
                            }

                            // MCP
                            for (Mappings mapping : NyaView.loader.mappings.values()) {
                                if (mapping.type == MappingType.MCP) {
                                    if (ColumnHelper.isAllowed("mcp/" + mapping.id)) {
                                        if (m.mcp.containsKey(mapping)) {
                                            r.add(m.mcp.get(mapping).name);
                                        } else {
                                            r.add("");
                                        }
                                    }
                                }
                            }

                            // Obfuscated Client
                            if (ColumnHelper.isAllowed("obfuscatedClient")) {
                                r.add(m.obfuscatedClient.name);
                            }

                            // Obfuscated Server
                            if (ColumnHelper.isAllowed("obfuscatedServer")) {
                                r.add(m.obfuscatedServer.name);
                            }

                            // Intermediary
                            for (var intermediary : NyaView.loader.intermediaries.values()) {
                                if (ColumnHelper.isAllowed("intermediary/" + intermediary.id)) {
                                    if (m.intermediary.containsKey(intermediary)) {
                                        r.add(m.intermediary.get(intermediary).name);
                                    } else {
                                        r.add("");
                                    }
                                }
                            }

                            // Babric
                            for (Mappings mapping : NyaView.loader.mappings.values()) {
                                if (mapping.type == MappingType.BABRIC) {
                                    if (ColumnHelper.isAllowed("fabric/" + mapping.id)) {
                                        if (m.babric.containsKey(mapping)) {
                                            r.add(m.babric.get(mapping).name);
                                            // TODO: Render Descriptors
                                            // r.add(Descriptor.niceString(m.babric.get(mapping)));
                                        } else {
                                            r.add("");
                                        }
                                    }
                                }
                            }

                            methodTable.addRow(r.toArray());
                        }
                        gui.methodTable.setModel(methodTable);
                        ColumnHelper.adjustColumns(gui.methodTable);
                        gui.methodTable.setEnabled(true);

                        // Fields
                        DefaultTableModel fieldTable = gui.initTableModel(true);

                        ArrayList<FieldMappingEntry> fields;
                        if (gui.currentSearchParameters.classDisplay == ClassDisplay.NONE || gui.currentSearchParameters.classDisplay == ClassDisplay.MINIMAL) {
                            fields = gui.currentSearch.results.get(c).fields;
                        } else {
                            fields = c.fields;
                        }

                        for (var f : fields) {
                            ArrayList<String> r = new ArrayList<>();

                            // MCP
                            for (Mappings mapping : NyaView.loader.mappings.values()) {
                                if (mapping.type == MappingType.MCP) {
                                    if (ColumnHelper.isAllowed("mcp/" + mapping.id)) {
                                        r.add(f.mcp.getOrDefault(mapping, ""));
                                    }
                                }
                            }

                            // Obfuscated Client
                            if (ColumnHelper.isAllowed("obfuscatedClient")) {
                                r.add(f.obfuscatedClient);
                            }

                            // Obfuscated Server
                            if (ColumnHelper.isAllowed("obfuscatedServer")) {
                                r.add(f.obfuscatedServer);
                            }

                            // Intermediary
                            for (var intermediary : NyaView.loader.intermediaries.values()) {
                                if (ColumnHelper.isAllowed("intermediary/" + intermediary.id)) {
                                    r.add(f.intermediary.getOrDefault(intermediary, ""));
                                }
                            }

                            // Babric
                            for (Mappings mapping : NyaView.loader.mappings.values()) {
                                if (mapping.type == MappingType.BABRIC) {
                                    if (ColumnHelper.isAllowed("fabric/" + mapping.id)) {
                                        r.add(f.babric.getOrDefault(mapping, ""));
                                    }
                                }
                            }

                            fieldTable.addRow(r.toArray());
                        }
                        gui.fieldTable.setModel(fieldTable);
                        ColumnHelper.adjustColumns(gui.fieldTable);
                        gui.fieldTable.setEnabled(true);
                    }

                } else {
                    gui.methodTable.setModel(gui.initTableModel(false));
                    gui.methodTable.setEnabled(false);
                    gui.fieldTable.setModel(gui.initTableModel(true));
                    gui.fieldTable.setEnabled(false);
                }
            }
        }
    }
}
