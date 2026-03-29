package com.interfaces;

import java.util.List;

public interface ITeam {
    String getName();
    String getLogoId();
    List<IPlayer> getSquad();
    List<IPlayer> getAvailablePlayers();
    ICoach getCoach(); // Baş antrenörü döner
    List<ICoach> getCoaches();
    ITactic getTactic();
    void setTactic(ITactic tactic);
    List<IPlayer> getStartingLineup();
    void setStartingLineup(List<IPlayer> players);
}
