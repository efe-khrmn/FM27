package com.abstracts;

import com.interfaces.IPlayer;
import java.util.Map;

public abstract class AbstractPlayer implements IPlayer, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected int number;
    protected int age;
    protected String position;
    protected int xp;
    protected int stamina;
    protected int injuryGames;
    protected int tacticCompatibility;

    public AbstractPlayer(String name, int number, int age, String position) {
        this.name = name;
        this.number = number;
        this.age = age;
        this.position = position;
        this.xp = 0;
        this.stamina = 100;
        this.injuryGames = 0;
        this.tacticCompatibility = 50;
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getNumber() { return number; }

    @Override
    public int getAge() { return age; }

    @Override
    public String getPosition() { return position; }

    @Override
    public int getXP() { return xp; }

    @Override
    public void addXP(int amount) {
        this.xp += amount;
        checkOverallProgression();
    }

    @Override
    public int getStamina() { return stamina; }

    @Override
    public void updateStamina(int delta) {
        this.stamina = Math.max(0, Math.min(100, this.stamina + delta));
    }

    @Override
    public int getTacticCompatibility() { return tacticCompatibility; }

    @Override
    public void setTacticCompatibility(int value) {
        this.tacticCompatibility = Math.max(0, Math.min(100, value));
    }

    @Override
    public boolean isInjured() { return injuryGames > 0; }

    @Override
    public int getInjuryGames() { return injuryGames; }

    @Override
    public void injure(int games) { this.injuryGames = games; }

    @Override
    public boolean isActive() { return !isInjured(); }

    public void decrementInjury() {
        if (injuryGames > 0) injuryGames--;
    }

    public void rest(int staminaDelta) {
        updateStamina(staminaDelta);
    }

    // Called internally after XP is added
    // Each sport defines its own threshold and progression logic
    protected abstract void checkOverallProgression();

    @Override
    public abstract Map<String, Integer> getOveralls();
}