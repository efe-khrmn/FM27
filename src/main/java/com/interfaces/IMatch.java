package com.interfaces;

import java.util.List;

public interface IMatch {
    ITeam getHomeTeam();
    ITeam getAwayTeam();
    void simulateNextSegment();
    void simulateOvertime();
    Object getCurrentScore();
    int getCurrentSegment();
    boolean isFinished();
    List<Object> getEvents();
    Object getResult();
}