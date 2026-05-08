package com.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlayerLoader {

    private static final List<String> names = new ArrayList<>();
    private static int cursor = 0;

    public static void load() {
        if (!names.isEmpty()) return;
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
            // NOTE: order preserved (no shuffle) so each team gets the same fixed 18 players every run.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Sequential fallback used if explicit team-slot mapping is not provided. */
    public static String getNext() {
        if (names.isEmpty()) load();
        if (names.isEmpty()) return "Player";
        String name = names.get(cursor % names.size());
        cursor++;
        return name;
    }

    /**
     * Deterministic mapping: team teamIndex (0-based) gets players
     * [teamIndex*squadSize .. teamIndex*squadSize + squadSize).
     */
    public static String getForTeam(int teamIndex, int slot, int squadSize) {
        if (names.isEmpty()) load();
        if (names.isEmpty()) return "Player " + (teamIndex * squadSize + slot + 1);
        int idx = (teamIndex * squadSize + slot) % names.size();
        return names.get(idx);
    }

    public static void reset() {
        cursor = 0;
        if (names.isEmpty()) load();
    }
}
