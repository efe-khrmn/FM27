package com.volleyball;

import com.abstracts.AbstractPlayer;
import com.interfaces.IPlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class VolleyballPlayer extends AbstractPlayer implements IPlayer {

    private Map<String, Integer> overalls;
    private int attackXP;
    private int defenseXP;

    public VolleyballPlayer(String name, int number, int age, String position,
                            int attackOverall, int defenseOverall) {
        super(name, number, age, position);
        this.overalls = new LinkedHashMap<>();
        this.overalls.put("attack", Math.max(1, Math.min(99, attackOverall)));
        this.overalls.put("defense", Math.max(1, Math.min(99, defenseOverall)));
        this.attackXP = 0;
        this.defenseXP = 0;
    }

    public void addAttackXP(int amount) {
        this.attackXP += amount;
        checkAttackProgression();
    }

    public void addDefenseXP(int amount) {
        this.defenseXP += amount;
        checkDefenseProgression();
    }

    private void checkAttackProgression() {
        int current = overalls.get("attack");
        int threshold = getThreshold(current);
        if (attackXP >= threshold) {
            overalls.put("attack", Math.min(99, current + 1));
            attackXP -= threshold;
        }
    }

    private void checkDefenseProgression() {
        int current = overalls.get("defense");
        int threshold = getThreshold(current);
        if (defenseXP >= threshold) {
            overalls.put("defense", Math.min(99, current + 1));
            defenseXP -= threshold;
        }
    }

    private int getThreshold(int current) {
        if (current < 70) return 100;
        else if (current < 80) return 150;
        else if (current < 90) return 200;
        else return 300;
    }

    @Override
    protected void checkOverallProgression() {
        // not used — addAttackXP and addDefenseXP handle progression
    }

    @Override
    public Map<String, Integer> getOveralls() { return overalls; }

    public int getAttackOverall() { return overalls.get("attack"); }
    public int getDefenseOverall() { return overalls.get("defense"); }
    public int getAttackXP() { return attackXP; }
    public int getDefenseXP() { return defenseXP; }

    public double getEffectiveOverall(String assignedPosition) {
        double attack = overalls.get("attack");
        double defense = overalls.get("defense");
        double avg = (attack + defense) / 2.0;

        if (assignedPosition.equals(this.position)) return avg;
        return applyPositionPenalty(avg, assignedPosition);
    }

    private double applyPositionPenalty(double overall, String assignedPosition) {
        String ownGroup = getPositionGroup(this.position);
        String assignedGroup = getPositionGroup(assignedPosition);

        if (ownGroup.equals(assignedGroup)) return overall * 0.95;
        if (isAdjacentGroup(ownGroup, assignedGroup)) return overall * 0.90;
        return overall * 0.70;
    }

    private String getPositionGroup(String pos) {
        switch (pos) {
            case "OH": case "OPP": return "attack";
            case "MB": case "S":   return "midfield";
            case "L":              return "defense";
            default:               return "unknown";
        }
    }

    private boolean isAdjacentGroup(String g1, String g2) {
        if (g1.equals("attack") && g2.equals("midfield")) return true;
        if (g1.equals("midfield") && g2.equals("attack")) return true;
        if (g1.equals("midfield") && g2.equals("defense")) return true;
        if (g1.equals("defense") && g2.equals("midfield")) return true;
        return false;
    }
}