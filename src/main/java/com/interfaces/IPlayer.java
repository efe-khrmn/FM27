package com.interfaces;
import java.util.Map;

public interface IPlayer {
    String getName();
    int getNumber();
    int getAge();
    String getPosition();
    Map<String, Integer> getOveralls();
    int getXP();
    void addXP(int amount);
    int getStamina();
    void updateStamina(int delta);
    int getTacticCompatibility();
    void setTacticCompatibility(int value);
    boolean isInjured();
    int getInjuryGames();
    void injure(int games);
    boolean isActive();
}
