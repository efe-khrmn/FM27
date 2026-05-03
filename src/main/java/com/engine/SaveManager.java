package com.engine;

import java.io.*;

public class SaveManager {

    private static final String SAVE_FILE = "savegame.dat";

    public static void save() throws IOException {
        GameState gs = GameState.getInstance();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(gs.getSport().getSportName());
            oos.writeObject(gs.getLeague());
            oos.writeObject(gs.getManagedTeam());
            oos.writeInt(gs.getWeek());
            oos.writeObject(gs.getPhase());
        }
        System.out.println("Game saved.");
    }

    public static void load() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {
            String sportName = (String) ois.readObject();
            League league = (League) ois.readObject();
            Object managedTeam = ois.readObject();
            int week = ois.readInt();
            Phase phase = (Phase) ois.readObject();

            GameState gs = GameState.getInstance();
            gs.setSport(SportFactory.create(sportName));
            gs.setLeague(league);
            gs.setManagedTeam((com.interfaces.ITeam) managedTeam);
            gs.setLeague(league);
            System.out.println("Game loaded.");
        }
    }
}
