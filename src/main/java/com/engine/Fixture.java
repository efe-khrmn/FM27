package com.engine;

import com.interfaces.ITeam;

public class Fixture {

    private ITeam homeTeam;
    private ITeam awayTeam;
    private int week;
    private Object result;

    public Fixture(ITeam homeTeam, ITeam awayTeam, int week) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.week = week;
        this.result = null;
    }

    public ITeam getHomeTeam() { return homeTeam; }
    public ITeam getAwayTeam() { return awayTeam; }
    public int getWeek() { return week; }
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
    public boolean isPlayed() { return result != null; }
}