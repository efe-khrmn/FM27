package com.engine;

import com.interfaces.ISport;
import com.interfaces.ITeam;

public class GameState {

    private static GameState instance;

    private ISport sport;
    private League league;
    private ITeam managedTeam;
    private int week;
    private Phase phase;

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
        this.league = new League(sport);
        this.week = 1;
        this.phase = Phase.TRAINING_WEEK;
    }

    public void nextPhase() {
        switch (phase) {
            case PREGAME:       phase = Phase.TRAINING_WEEK; break;
            case TRAINING_WEEK: phase = Phase.PRE_MATCH;     break;
            case PRE_MATCH:     phase = Phase.IN_MATCH;      break;
            case IN_MATCH:      phase = Phase.POST_MATCH;    break;
            case POST_MATCH:
                if (league.isSeasonOver()) {
                    phase = Phase.SEASON_END;
                } else {
                    week++;
                    phase = Phase.TRAINING_WEEK;
                }
                break;
            case SEASON_END:    phase = Phase.PREGAME;       break;
        }
    }

    public ISport getSport() { return sport; }
    public League getLeague() { return league; }
    public ITeam getManagedTeam() { return managedTeam; }
    public int getWeek() { return week; }
    public Phase getPhase() { return phase; }

    public void setSport(ISport sport) { this.sport = sport; }
    public void setManagedTeam(ITeam managedTeam) { this.managedTeam = managedTeam; }
}