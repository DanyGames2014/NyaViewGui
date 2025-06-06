package net.danygames2014.nyaviewgui.gui;

import net.danygames2014.nyaview.ActionResult;
import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.download.Downloader;
import net.danygames2014.nyaviewgui.ColorUtil;
import net.danygames2014.nyaviewgui.NyaViewGui;
import net.danygames2014.nyaviewgui.Util;

import javax.swing.*;
import java.awt.*;

public class OptionsGui extends JFrame {
    /* Main Layout */
    public JPanel mainPanel;
    public GridLayout mainLayout;
    
    public JPanel intermediariesPanel;
    public GridBagLayout intermediariesLayout;
    
    public JPanel mappingsPanel;
    public GridBagLayout mappingsLayout;
    
    public JPanel downloadPanel;
    public GridBagLayout downloadLayout;

    public Font font;

    public OptionsGui(MappingGui parent) throws HeadlessException {
        super("Options");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initialize();

        this.setSize(640, 640);
        this.setVisible(true);
    }

    public void initialize() {
        // Main Panel
        mainLayout = new GridLayout(3,1);
        mainPanel = new JPanel(mainLayout);

        font = new Font(UIManager.getFont("Label.font").getName(), Font.PLAIN, 20);
        
        // Intermediaries
        initIntermediaries();
        mainPanel.add(intermediariesPanel);
        
        // Mappings
        initMappings();
        mainPanel.add(mappingsPanel);
        
        // Download
        initDownloads();
        mainPanel.add(downloadPanel);
        
        // Add main panel to the frame
        this.add(mainPanel);
    }
    
    public void initIntermediaries(){
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
                ActionResult result = NyaView.config.removeIntermediaries(item.getKey());
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
    
    public void initMappings(){
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
            
            JButton downloadButton = new JButton("Remove");
            downloadButton.addActionListener(e -> {
                ActionResult result = NyaView.config.removeMappings(item.getKey());
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

            mappingsPanel.add(panel, c);
        }
    }
    
    public void initDownloads(){
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

        var downloadable = NyaView.downloadCatalog.getCatalog(NyaView.config.getDownloadVersion());
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
    
    public void reloadWindow(){
        this.getContentPane().removeAll();
        this.initialize();
        this.revalidate();
        this.repaint();
    }
    
    public void reInitMappings(){
        NyaView.init();
        NyaViewGui.mappingGui.initColumnFilters();
        NyaViewGui.mappingGui.initTableModels();
        NyaViewGui.mappingGui.refreshTableContents();
        NyaViewGui.mappingGui.search("");
    }
}
