package net.danygames2014.nyaviewgui.gui;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.profile.Profile;

import javax.swing.*;
import java.awt.*;

public class CreateProfileGui extends JFrame {
    private final SetupGui parent;

    /* Main Layout */
    public JPanel mainPanel;
    public BorderLayout mainLayout;

    public JPanel topPanel;
    public FlowLayout topLayout;

    public JPanel centerPanel;
    public GridBagLayout centerLayout;

    public JPanel bottomPanel;
    public FlowLayout bottomLayout;

    public CreateProfileGui(SetupGui parent) throws HeadlessException {
        super("New Profile");
        this.parent = parent;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initialize();

        this.setSize(400, 600);
        this.setResizable(false);
        this.setVisible(true);
    }

    public void initialize() {
        // Main Panel
        this.mainLayout = new BorderLayout();
        this.mainPanel = new JPanel(mainLayout);

        // Top Panel
        this.topLayout = new FlowLayout(FlowLayout.CENTER);
        this.topPanel = new JPanel(topLayout);
        JLabel topLabel = new JLabel("Create New Profile");
        topLabel.setFont(parent.font);
        this.topPanel.add(topLabel);
        this.mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel
        this.centerLayout = new GridBagLayout();
        this.centerPanel = new JPanel(centerLayout);

        JTextField idField = new JTextField();
        idField.setEditable(false);
        this.centerPanel.add(idField);
        
        JTextField nameField = new JTextField();
        
        nameField.addCaretListener(e -> {
            idField.setText(nameField.getText().replaceAll("[^a-zA-Z1-9_+*-]", "").toLowerCase());
        });
        
        this.centerPanel.add(nameField);

        JTextField versionField = new JTextField();
        this.centerPanel.add(versionField);

        this.mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        this.bottomLayout = new FlowLayout(FlowLayout.CENTER);
        this.bottomPanel = new JPanel(bottomLayout);

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> {
            if (idField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "ID is empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            nameField.setText(nameField.getText().replaceAll("[^a-zA-Z0-9_+*., -]", ""));
            if (nameField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Name is empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (versionField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Version is empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Profile profile = new Profile(Profile.constructProfilePath(idField.getText()), idField.getText(), nameField.getText(), versionField.getText());
            NyaView.profileManager.addProfile(profile);
            profile.save();

            parent.updateProfile(profile);
            this.dispose();

        });
        this.bottomPanel.add(createButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            this.dispose();
        });
        this.bottomPanel.add(cancelButton);

        this.mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        this.add(mainPanel);
    }
}
