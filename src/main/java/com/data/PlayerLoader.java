package com.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerLoader {

    private static List<String> names = new ArrayList<>();

    public static void load() {
        try {
            InputStream is = PlayerLoader.class
                    .getClassLoader().getResourceAsStream("players.txt");
            if (is == null) {
                System.out.println("players.txt not found, using defaults.");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) names.add(line.trim());
            }
            reader.close();
            Collections.shuffle(names);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNext() {
        if (names.isEmpty()) load();
        if (names.isEmpty()) return "Player";
        String name = names.remove(0);
        // if running low, reload
        if (names.size() < 10) {
            load();
            Collections.shuffle(names);
        }
        return name;
    }

    public static void reset() {
        names.clear();
        load();
    }
}