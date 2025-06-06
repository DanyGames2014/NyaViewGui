package net.danygames2014.nyaviewgui.gui;

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
}
