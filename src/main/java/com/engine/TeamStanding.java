package com.engine;

import com.interfaces.ITeam;

import java.io.Serializable;

public class TeamStanding implements Serializable {
    private static final long serialVersionUID = 1L;
    private ITeam team;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int scored;
    private int conceded;
    private int points;

    public TeamStanding(ITeam team) {
        this.team = team;
        this.played = 0;
        this.won = 0;
        this.drawn = 0;
        this.lost = 0;
        this.scored = 0;
        this.conceded = 0;
        this.points = 0;
    }

    public void update(int goalsFor, int goalsAgainst, int pointsEarned) {
        this.played++;
        this.scored += goalsFor;
        this.conceded += goalsAgainst;
        this.points += pointsEarned;

        if (pointsEarned == 3) won++;
        else if (pointsEarned == 1) drawn++;
        else lost++;
    }

    public ITeam getTeam() { return team; }
    public int getPlayed() { return played; }
    public int getWon() { return won; }
    public int getDrawn() { return drawn; }
    public int getLost() { return lost; }
    public int getScored() { return scored; }
    public int getConceded() { return conceded; }
    public int getPoints() { return points; }
    public int getGoalDiff() { return scored - conceded; }
    public double getAverage() {
        if (conceded == 0) return scored;
        return (double) scored / conceded;
    }
}
