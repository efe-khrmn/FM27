package com.football;

import com.interfaces.ICoach;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;
import com.interfaces.ITrainingSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FootballTraining implements ITrainingSession, Serializable {
    private static final long serialVersionUID = 1L;
    private ITeam team;
    private List<Object> results;
    private static final List<String> TRAINING_TYPES = new ArrayList<>();

    static {
        TRAINING_TYPES.add("Fitness");
        TRAINING_TYPES.add("Attack");
        TRAINING_TYPES.add("Defense");
        TRAINING_TYPES.add("Tactic");
    }

    public FootballTraining(ITeam team) {
        this.team = team;
        this.results = new ArrayList<>();
    }

    @Override
    public List<String> getTrainingTypes() { return TRAINING_TYPES; }

    @Override
    public void runTraining(String type) {
        results.clear();
        ICoach headCoach = team.getCoach();
        int coachLevel = (headCoach != null) ? headCoach.getExperienceLevel() : 1;

        for (IPlayer player : team.getSquad()) {
            int staminaDelta = computeStaminaDelta(type);
            player.updateStamina(staminaDelta);
            player.setTacticCompatibility(
                    Math.min(100, player.getTacticCompatibility() + 5)
            );

            if (player instanceof FootballPlayer) {
                FootballPlayer fp = (FootballPlayer) player;
                applyXPGain(fp, coachLevel, type);
            }

            results.add("Player: " + player.getName()
                    + " | Stamina " + staminaDelta
                    + " | Compat +5");
        }
    }

    private void applyXPGain(FootballPlayer player, int coachLevel, String type) {
        int xpGain = computeXPGain(player.getAge(), coachLevel);

        switch (type) {
            case "Attack":
                player.addAttackXP(xpGain);
                break;
            case "Defense":
                player.addDefenseXP(xpGain);
                break;
            case "Fitness":
            case "Tactic":
                player.addAttackXP(xpGain);
                player.addDefenseXP(xpGain);
                break;
        }
    }

    private int computeXPGain(int age, int coachLevel) {
        int base = 10;
        if (age < 23) base += 4;
        else if (age > 30) base -= 3;
        base += coachLevel * 2;
        return base;
    }

    private int computeStaminaDelta(String type) {
        switch (type) {
            case "Fitness":  return -5;
            case "Attack":   return -8;
            case "Defense":  return -8;
            case "Tactic":   return -4;
            default:         return -6;
        }
    }

    @Override
    public List<Object> getResults() { return results; }
}