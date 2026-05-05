package com.engine;

import java.io.*;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.dat";

    public static void save() throws IOException {
        GameState gs = GameState.getInstance();

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {

            oos.writeObject(gs);
        }

        System.out.println("Game saved.");
    }

    public static void load() throws IOException, ClassNotFoundException {
        File file = new File(SAVE_FILE);

        if (!file.exists()) {
            System.out.println("No save file found.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {

            GameState loaded = (GameState) ois.readObject();
            GameState.setInstance(loaded);
        }

        System.out.println("Game loaded.");
    }
}