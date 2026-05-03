package com.engine;

import com.interfaces.IPlayer;

public class TrainingResult {
    private static final long serialVersionUID = 1L;
    private IPlayer player;
    private int attackXPGained;
    private int defenseXPGained;
    private int staminaDelta;
    private int compatDelta;
    private boolean overallChanged;

    public TrainingResult(IPlayer player, int attackXPGained, int defenseXPGained,
                          int staminaDelta, int compatDelta, boolean overallChanged) {
        this.player = player;
        this.attackXPGained = attackXPGained;
        this.defenseXPGained = defenseXPGained;
        this.staminaDelta = staminaDelta;
        this.compatDelta = compatDelta;
        this.overallChanged = overallChanged;
    }

    public IPlayer getPlayer() { return player; }
    public int getAttackXPGained() { return attackXPGained; }
    public int getDefenseXPGained() { return defenseXPGained; }
    public int getStaminaDelta() { return staminaDelta; }
    public int getCompatDelta() { return compatDelta; }
    public boolean isOverallChanged() { return overallChanged; }

    @Override
    public String toString() {
        return player.getName()
                + " | AttackXP +" + attackXPGained
                + " | DefenseXP +" + defenseXPGained
                + " | Stamina " + staminaDelta
                + " | Compat +" + compatDelta
                + " | Overall changed: " + overallChanged;
    }
}