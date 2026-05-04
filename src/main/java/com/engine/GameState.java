package com.engine;

import com.interfaces.ISport;
import com.interfaces.ITeam;

public class GameState {
    private static final long serialVersionUID = 1L;
    private static GameState instance;

    private ISport sport;
    private League league;
    private ITeam managedTeam;
    private int week;
    private Phase phase;
    private Object lastMatchResult;

    private GameState() {
        this.week = 1;
        this.phase = Phase.PREGAME;
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void newGame(ISport sport, ITeam managedTeam) {
        this.sport = sport;
        this.managedTeam = managedTeam;
        this.week = 1;
        this.phase = Phase.TRAINING_WEEK;
        this.lastMatchResult = null;
    }

    public void nextPhase() {
        switch (phase) {
            case PREGAME:
                phase = Phase.TRAINING_WEEK;
                break;
            case TRAINING_WEEK:
                phase = Phase.PRE_MATCH;
                break;
            case PRE_MATCH:
                phase = Phase.IN_MATCH;
                break;
            case IN_MATCH:
                phase = Phase.POST_MATCH;
                break;
            case POST_MATCH:
                if (league.isSeasonOver()) {
                    phase = Phase.SEASON_END;
                } else {
                    week++;
                    phase = Phase.TRAINING_WEEK;
                }
                break;
            case SEASON_END:
                phase = Phase.PREGAME;
                break;
        }
    }
    private int lastHomeScore = -1;
    private int lastAwayScore = -1;
    private ITeam lastHomeTeam = null;
    private ITeam lastAwayTeam = null;

    public void setLastMatchScore(ITeam home, int homeScore, ITeam away, int awayScore) {
        this.lastHomeTeam = home;
        this.lastHomeScore = homeScore;
        this.lastAwayTeam = away;
        this.lastAwayScore = awayScore;
    }

    public int getLastHomeScore() { return lastHomeScore; }
    public int getLastAwayScore() { return lastAwayScore; }
    public ITeam getLastHomeTeam() { return lastHomeTeam; }
    public ITeam getLastAwayTeam() { return lastAwayTeam; }
    // ── Getters ──
    public ISport getSport() { return sport; }
    public League getLeague() { return league; }
    public ITeam getManagedTeam() { return managedTeam; }
    public int getWeek() { return week; }
    public Phase getPhase() { return phase; }
    public Object getLastMatchResult() { return lastMatchResult; }

    // ── Setters ──
    public void setSport(ISport sport) { this.sport = sport; }
    public void setLeague(League league) { this.league = league; }
    public void setWeek(int week) { this.week = week; }
    public void setPhase(Phase phase) { this.phase = phase; }
    public void advanceWeek() {
        this.week++;
        this.phase = Phase.TRAINING_WEEK;
    }
    public void setManagedTeam(ITeam managedTeam) { this.managedTeam = managedTeam; }
    public void setLastMatchResult(Object result) { this.lastMatchResult = result; }
}