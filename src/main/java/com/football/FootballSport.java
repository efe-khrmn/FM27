package com.football;


import com.abstracts.AbstractSport;
import com.interfaces.ISport;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;
import com.interfaces.IMatch;
import com.interfaces.ITrainingSession;

import com.interfaces.IStandingsRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FootballSport extends AbstractSport implements ISport {

    private final IStandingsRules standingsRules = new FootballStandingsRules();
    private final Random random = new Random();
    private int playerNumberCounter = 1;

    @Override
    public String getSportName() {
        return "Football";
    }

    @Override
    public int getTeamSize() {
        return 11;
    }

    @Override
    public int getMaxSubstitutions() {
        return 3;
    }

    @Override
    public int getSegmentCount() {
        return 2;
    }

    @Override
    public String getSegmentLabel(int index) {
        return "Half";
    }

    @Override
    public boolean hasOvertime() {
        return false;
    }

    @Override
    public List<String> getPositions() {
        List<String> positions = new ArrayList<>();
        positions.add("GK");
        positions.add("CB");
        positions.add("LB");
        positions.add("RB");
        positions.add("CM");
        positions.add("CAM");
        positions.add("LW");
        positions.add("RW");
        positions.add("ST");
        return positions;
    }

    @Override
    public List<String> getOverallTypes() {
        List<String> types = new ArrayList<>();
        types.add("Attack");
        types.add("Defense");
        return types;
    }

    @Override
    public IStandingsRules getStandingsRules() {
        return standingsRules;
    }

    @Override
    public IPlayer createPlayer(String name, String position) {
        int age = 18 + random.nextInt(18);
        int attack;
        int defense;

        if (position.equals("GK")) {
            attack = 5 + random.nextInt(21);
            defense = 70 + random.nextInt(21);
        } else {
            attack = 40 + random.nextInt(46);
            defense = 40 + random.nextInt(46);
        }
        return new FootballPlayer(name, playerNumberCounter++, age, position, attack, defense);
    }

    @Override
    public ITeam createTeam(String name, String logoId) {
        return new FootballTeam(name, logoId);
    }

    @Override
    public IMatch createMatch(ITeam home, ITeam away) {
        return new FootballMatch(home, away);
    }

    @Override
    public ITrainingSession createTraining(ITeam team) {
        return new FootballTraining(team);
    }

    public void resetPlayerNumberCounter() {
        this.playerNumberCounter = 1;
    }
}