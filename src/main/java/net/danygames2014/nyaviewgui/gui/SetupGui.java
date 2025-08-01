package net.danygames2014.nyaviewgui.gui;

import net.danygames2014.nyaview.ActionResult;
import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.download.Downloader;
import net.danygames2014.nyaview.profile.Profile;
import net.danygames2014.nyaviewgui.ColorUtil;
import net.danygames2014.nyaviewgui.NyaViewGui;
import net.danygames2014.nyaviewgui.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SetupGui extends JFrame {
    private final MappingGui parent;
    
    /* Main Layout */
    public JPanel mainPanel;
    public BorderLayout mainLayout;

    /* Toolbar */
    public JPanel toolbarPanel;
    public GridBagLayout toolbarLayout;

    // Left Toolbar
    public JPanel leftToolbarPanel;
    public FlowLayout leftToolbarLayout;

    public JLabel profileLabel;
    
    // Right Toolbar
    public JPanel rightToolbarPanel;
    public FlowLayout rightToolbarLayout;

    public JComboBox<Profile> profileComboBox;

    public JPanel contentPanel;
    public GridLayout contentLayout;

    public JPanel intermediariesPanel;
    public GridBagLayout intermediariesLayout;

    public JPanel mappingsPanel;
    public GridBagLayout mappingsLayout;

    public JPanel downloadPanel;
    public GridBagLayout downloadLayout;

    public Font font;
    public Font smallFont;

    public SetupGui(MappingGui parent) throws HeadlessException {
        super("Options");
        this.parent = parent;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initialize();

        this.setSize(640, 640);
        this.setVisible(true);
    }

    public void initialize() {
        // Main Panel
        mainLayout = new BorderLayout();
        mainPanel = new JPanel(mainLayout);

        // Content Panel
        contentLayout = new GridLayout(2, 2);
        contentPanel = new JPanel(contentLayout);

        font = new Font(UIManager.getFont("Label.font").getName(), Font.PLAIN, 20);
        smallFont = new Font(UIManager.getFont("Label.font").getName(), Font.PLAIN, 16);

        // Top Bar
        initTopBar();
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Intermediaries
        initIntermediaries();
        contentPanel.add(intermediariesPanel);

        // Mappings
        initMappings();
        contentPanel.add(mappingsPanel);

        // Download
        initDownloads();
        contentPanel.add(downloadPanel);

        // Add Content Panel to the Main Panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to the frame
        this.add(mainPanel);
    }

    public void initTopBar() {
        // Toolbar Panel
        toolbarLayout = new GridBagLayout();
        leftToolbarLayout = new FlowLayout(FlowLayout.LEFT);
        rightToolbarLayout = new FlowLayout(FlowLayout.RIGHT);

        toolbarPanel = new JPanel(toolbarLayout);
        toolbarPanel.setBackground(ColorUtil.getAlternatePanelColor());

        // Left Toolbar
        leftToolbarPanel = new JPanel(leftToolbarLayout);
        leftToolbarPanel.setBackground(ColorUtil.getAlternatePanelColor());

        Profile activeProfile = NyaView.profileManager.activeProfile;
        profileLabel = new JLabel();
        updateProfileLabel(activeProfile);
        profileLabel.setFont(smallFont);
        leftToolbarPanel.add(profileLabel);
        
        GridBagConstraints leftToolbarConstraint = new GridBagConstraints();
        leftToolbarConstraint.fill = GridBagConstraints.VERTICAL;
        leftToolbarConstraint.gridx = 0;
        leftToolbarConstraint.gridy = 0;
        leftToolbarConstraint.weightx = 0.5;
        leftToolbarConstraint.insets = new Insets(0, 5, 0, 0);
        leftToolbarConstraint.anchor = GridBagConstraints.LINE_START;

        toolbarPanel.add(leftToolbarPanel, leftToolbarConstraint);

        // Right Toolbar
        rightToolbarPanel = new JPanel(rightToolbarLayout);
        rightToolbarPanel.setBackground(ColorUtil.getAlternatePanelColor());

        JLabel profileSelectLabel = new JLabel("Select Profile: ");
        profileSelectLabel.setFont(smallFont);
        rightToolbarPanel.add(profileSelectLabel);
        
        initProfileComboBox();
        
        JButton newProfileButton = new JButton("Create Profile");
        newProfileButton.addActionListener(e -> {
            CreateProfileGui createProfileGui = new CreateProfileGui(this);
        });
        rightToolbarPanel.add(newProfileButton);

        GridBagConstraints rightToolbarConstraint = new GridBagConstraints();
        rightToolbarConstraint.fill = GridBagConstraints.VERTICAL;
        rightToolbarConstraint.gridx = 1;
        rightToolbarConstraint.gridy = 0;
        rightToolbarConstraint.weightx = 0.5;
        rightToolbarConstraint.insets = new Insets(0, 0, 0, 5);
        rightToolbarConstraint.anchor = GridBagConstraints.LINE_END;

        toolbarPanel.add(rightToolbarPanel, rightToolbarConstraint);
    }
    
    public void updateProfileLabel(Profile profile) {
        profileLabel.setText("Active Profile: " + profile.getName() + " (" + profile.getVersion() + ")");
    }
    
    public void updateProfile(Profile profile) {
        NyaView.profileManager.switchProfile(profile.getId());
        NyaView.loadMappings();
        parent.updateWindowTitle();
        parent.initColumnFilters();
        parent.initTableModels();
        parent.refreshTableContents();
        parent.search("");
        parent.searchField.setText("");
        updateProfileLabel(profile);
        initProfileComboBox();
        reloadContent();
    }
    
    public void initProfileComboBox() {
        if (profileComboBox != null) {
            rightToolbarPanel.remove(profileComboBox);
        }
        
        profileComboBox = new JComboBox<>();
        profileComboBox.setModel(new DefaultComboBoxModel<>(NyaView.profileManager.getProfiles().values().toArray(new Profile[0])));
        profileComboBox.setSelectedItem(NyaView.profileManager.activeProfile);

        profileComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem() instanceof Profile profile) {
                    updateProfile(profile);
                }
            }
        });

        profileComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Profile profile) {
                    setText(profile.getName());
                }
                return this;
            }
        });

        rightToolbarPanel.add(profileComboBox);
    }
    
    public void reloadContent() {
        contentPanel.removeAll();
        
        // Intermediaries
        initIntermediaries();
        contentPanel.add(intermediariesPanel);

        // Mappings
        initMappings();
        contentPanel.add(mappingsPanel);

        // Download
        initDownloads();
        contentPanel.add(downloadPanel);
    }

    public void initIntermediaries() {
        intermediariesLayout = new GridBagLayout();
        intermediariesPanel = new JPanel(intermediariesLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 1.0;
        c.insets = new Insets(3, 15, 3, 15);
        c.anchor = GridBagConstraints.LINE_START;

        JLabel intsLabel = new JLabel(" Intermediaries");
        intsLabel.setFont(font);
        intermediariesPanel.add(intsLabel, c);

        c.anchor = GridBagConstraints.CENTER;

        for (var item : NyaView.loader.intermediaries.entrySet()) {
            GridBagLayout layout = new GridBagLayout();
            JPanel panel = new JPanel(layout);
            panel.setBackground(ColorUtil.getAlternatePanelColor());

            GridBagConstraints cs = new GridBagConstraints();
            cs.insets = new Insets(3, 10, 3, 10);

            // Name
            cs.anchor = GridBagConstraints.LINE_START;
            cs.gridx = 0;
            cs.gridy = 0;
            cs.weightx = 1.0;
            panel.add(new JLabel("  " + item.getValue().name), cs);

            // Remove Button
            JButton editButton = new JButton("Edit");
            editButton.addActionListener(e -> {
                ActionResult result = new ActionResult(69420, "Not Yet Implemented");
                Util.showDialog(this, result);
            });

            JButton downloadButton = new JButton("Remove");
            downloadButton.addActionListener(e -> {
                ActionResult result = NyaView.profileManager.activeProfile.removeIntermediaries(item.getKey());
                Util.showDialog(this, result);
                reInitMappings();
                reloadWindow();
            });

            cs.anchor = GridBagConstraints.LINE_END;
            cs.weightx = 0;

            cs.gridx = 1;
            panel.add(editButton, cs);

            cs.gridx = 2;
            panel.add(downloadButton, cs);

            intermediariesPanel.add(panel, c);
        }
    }

    public void initMappings() {
        mappingsLayout = new GridBagLayout();
        mappingsPanel = new JPanel(mappingsLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 1.0;
        c.insets = new Insets(3, 15, 3, 15);
        c.anchor = GridBagConstraints.LINE_START;

        JLabel mapsLabel = new JLabel(" Mappings");
        mapsLabel.setFont(font);
        mappingsPanel.add(mapsLabel, c);

        c.anchor = GridBagConstraints.CENTER;

        for (var item : NyaView.loader.mappings.entrySet()) {
            GridBagLayout layout = new GridBagLayout();
            JPanel panel = new JPanel(layout);
            panel.setBackground(ColorUtil.getAlternatePanelColor());

            GridBagConstraints cs = new GridBagConstraints();
            cs.insets = new Insets(3, 10, 3, 10);

            // Name
            cs.anchor = GridBagConstraints.LINE_START;
            cs.gridx = 0;
            cs.gridy = 0;
            cs.weightx = 1.0;
            panel.add(new JLabel("  " + item.getValue().name), cs);

            // Remove Button
            JButton editButton = new JButton("Edit");
            editButton.addActionListener(e -> {
                ActionResult result = new ActionResult(69420, "Not Yet Implemented");
                Util.showDialog(this, result);
            });

            JButton removeButton = new JButton("Remove");
            removeButton.addActionListener(e -> {
                ActionResult result = NyaView.profileManager.activeProfile.removeMappings(item.getKey());
                Util.showDialog(this, result);
                reInitMappings();
                reloadWindow();
            });

            cs.anchor = GridBagConstraints.LINE_END;
            cs.weightx = 0;

            cs.gridx = 1;
            panel.add(editButton, cs);

            cs.gridx = 2;
            panel.add(removeButton, cs);

            mappingsPanel.add(panel, c);
        }
    }

    public void initDownloads() {
        downloadLayout = new GridBagLayout();
        downloadPanel = new JPanel(downloadLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 0.7;
        c.insets = new Insets(3, 15, 3, 15);
        c.anchor = GridBagConstraints.LINE_START;

        // Label
        JLabel dlLabel = new JLabel(" Avalible Downloads");
        dlLabel.setFont(font);
        downloadPanel.add(dlLabel, c);

        c.anchor = GridBagConstraints.CENTER;

        var downloadable = NyaView.downloadCatalog.getCatalog(NyaView.profileManager.activeProfile.getVersion());
        for (var dl : downloadable.entrySet()) {
            GridBagLayout layout = new GridBagLayout();
            JPanel panel = new JPanel(layout);
            panel.setBackground(ColorUtil.getAlternatePanelColor());

            GridBagConstraints cs = new GridBagConstraints();
            cs.insets = new Insets(3, 10, 3, 10);

            // Name
            cs.anchor = GridBagConstraints.LINE_START;
            cs.gridx = 0;
            cs.weightx = 1.0;
            panel.add(new JLabel("  " + dl.getValue().getName()), cs);

            // Download Button
            JButton downloadButton = new JButton("Download");
            downloadButton.addActionListener(e -> {
                ActionResult result = Downloader.download(dl.getValue());
                Util.showDialog(this, result);
                reInitMappings();
                reloadWindow();
            });
            cs.anchor = GridBagConstraints.LINE_END;
            cs.gridx = 1;
            cs.weightx = 1.0;
            panel.add(downloadButton, cs);

            // Add to Downloads
            downloadPanel.add(panel, c);
        }
    }

    public void reloadWindow() {
        this.getContentPane().removeAll();
        this.initialize();
        this.revalidate();
        this.repaint();
    }

    public void reInitMappings() {
        NyaView.init();
        NyaViewGui.mappingGui.initColumnFilters();
        NyaViewGui.mappingGui.initTableModels();
        NyaViewGui.mappingGui.refreshTableContents();
        NyaViewGui.mappingGui.search("");
    }
}
