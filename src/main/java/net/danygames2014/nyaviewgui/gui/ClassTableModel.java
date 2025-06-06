package net.danygames2014.nyaviewgui.gui;

import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class ClassTableModel extends DefaultTableModel {
    private final ArrayList<ClassMappingEntry> classMappingEntries;

    public ClassTableModel() {
        this.classMappingEntries = new ArrayList<>();
    }

    public void addRow(Object[] rowData, ClassMappingEntry classEntry) {
        classMappingEntries.add(classEntry);
        super.addRow(rowData);
    }

    public ClassMappingEntry getClassMappingEntry(int row) {
        return classMappingEntries.get(row);
    }
}
