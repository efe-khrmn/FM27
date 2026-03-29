package com.interfaces;

import java.util.List;

public interface ISport {
    String getSportName();
    int getTeamSize();
    int getMaxSubstitutions();
    int getSegmentCount();
    String getSegmentLabel(int index);
    boolean hasOvertime();
    List<String> getPositions();
    List<String> getOverallTypes();
    IStandingsRules getStandingsRules();


    IPlayer createPlayer(String name, String position);
    ITeam createTeam(String name, String logoId);
    IMatch createMatch(ITeam home, ITeam away);
    ITrainingSession creatıneTraining(ITeam team);
}