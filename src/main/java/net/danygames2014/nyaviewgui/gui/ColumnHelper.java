package net.danygames2014.nyaviewgui.gui;

import net.danygames2014.nyaviewgui.NyaViewGui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;

public class ColumnHelper {
    public static HashMap<String, Boolean> allowedColumns = new HashMap<>();

    public static void allow(String key) {
        allowedColumns.put(key, true);
        System.out.println("Allowing column " + key);
    }

    public static void hide(String key) {
        allowedColumns.put(key, false);
        System.out.println("Hiding column " + key);
    }

    public static void clear() {
        allowedColumns.clear();
    }

    public static void add(String key) {
        allowedColumns.put(key, true);
    }

    public static boolean isAllowed(String key) {
        return allowedColumns.getOrDefault(key, false);
    }
    
    @SuppressWarnings("ExtractMethodRecommender")
    public static void adjustColumns(JTable table) {
        boolean compactObfuscatedColumns = NyaViewGui.guiConfig.getOption("compactObfuscatedColumns");
        boolean horizontalScrollbars = NyaViewGui.guiConfig.getOption("horizontalScrollbars");
        
        for (int column = 0; column < table.getColumnCount(); column++) {
            String columnName = table.getTableHeader().getColumnModel().getColumn(column).getHeaderValue().toString();
            
            // Special handling for obfuscated columns
            if (columnName.startsWith("Obf")) {
                String[] split = columnName.split(" ");
                String newColumnName = columnName;
                
                if (split.length > 1) {
                    if (split[1].startsWith("C")) {
                        if (compactObfuscatedColumns) {
                            newColumnName = "Obf C.";
                        } else {
                            newColumnName = "Obfuscated Client";
                        }
                    } else if (split[1].startsWith("S")) {
                        if (compactObfuscatedColumns) {
                            newColumnName = "Obf S.";
                        } else {
                            newColumnName = "Obfuscated Server";
                        }
                    }
                }
                
                table.getColumnModel().getColumn(column).setHeaderValue(newColumnName);
            }

            // First get the preferred width of the header
            int width = table.getTableHeader().getColumnModel().getColumn(column).getWidth();
            
            // Get the width of each row in the column
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width, width);
            }
            
            // Put atleast some limit to the maximum width
            width = Math.min(width, 1000);
            
            table.getColumnModel().getColumn(column).setPreferredWidth(width + 5);
        }
    }
}
