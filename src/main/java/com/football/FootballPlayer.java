package com.football;

import com.abstracts.AbstractPlayer;
import com.interfaces.IPlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class FootballPlayer extends AbstractPlayer implements IPlayer {
    private int attackOverall;
    private int defenseOverall;
    private static final int XP_THRESHOLD = 50;

    public FootballPlayer(String name, int number, int age, String position,
                          int attackOverall, int defenseOverall) {
        super(name, number, age, position);
        this.attackOverall = Math.max(1, Math.min(99, attackOverall));
        this.defenseOverall = Math.max(1, Math.min(99, defenseOverall));
    }

    @Override
    public Map<String, Integer> getOveralls() {
        Map<String, Integer> overalls = new LinkedHashMap<>();
        overalls.put("Attack", attackOverall);
        overalls.put("Defense", defenseOverall);
        return overalls;
    }

    public int getAttackOverall() {
        return attackOverall;
    }

    public int getDefenseOverall() {
        return defenseOverall;
    }


    @Override
    protected void checkOverallProgression() {
        while (xp >= XP_THRESHOLD) {
            xp -= XP_THRESHOLD;

            if (attackOverall <= defenseOverall) {
                attackOverall = Math.min(99, attackOverall + 1);
            } else {
                defenseOverall = Math.min(99, defenseOverall + 1);
            }
        }
    }
}