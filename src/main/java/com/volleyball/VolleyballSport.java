package com.volleyball;

import com.abstracts.AbstractSport;
import com.interfaces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VolleyballSport extends AbstractSport {

    private static final List<String> POSITIONS = new ArrayList<>();
    private static final List<String> OVERALL_TYPES = new ArrayList<>();

    static {
        POSITIONS.add("OH");  // Outside Hitter
        POSITIONS.add("OPP"); // Opposite
        POSITIONS.add("MB");  // Middle Blocker
        POSITIONS.add("S");   // Setter
        POSITIONS.add("L");   // Libero

        OVERALL_TYPES.add("attack");
        OVERALL_TYPES.add("defense");
    }

    @Override
    public String getSportName() { return "Volleyball"; }

    @Override
    public int getTeamSize() { return 6; }

    @Override
    public int getMaxSubstitutions() { return -1; } // unlimited

    @Override
    public int getSegmentCount() { return 5; } // max 5 sets

    @Override
    public String getSegmentLabel(int index) {
        return "Set " + index;
    }

    @Override
    public boolean hasOvertime() { return false; }

    @Override
    public List<String> getPositions() { return POSITIONS; }

    @Override
    public List<String> getOverallTypes() { return OVERALL_TYPES; }

    @Override
    public IStandingsRules getStandingsRules() {
        return new VolleyballStandingsRules();
    }

    @Override
    public IPlayer createPlayer(String name, String position) {
        Random random = new Random();
        int age = 18 + random.nextInt(18);
        int number = 1 + random.nextInt(99);
        int attack = 40 + random.nextInt(40);
        int defense = 40 + random.nextInt(40);
        return new VolleyballPlayer(name, number, age, position, attack, defense);
    }

    @Override
    public ITeam createTeam(String name, String logoId) {
        return new VolleyballTeam(name, logoId, false);
    }

    @Override
    public IMatch createMatch(ITeam home, ITeam away) {
        return new VolleyballMatch(home, away);
    }

    @Override
    public ITrainingSession createTraining(ITeam team) {
        return new VolleyballTraining(team);
    }
}