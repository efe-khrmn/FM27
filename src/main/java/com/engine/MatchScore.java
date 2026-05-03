package com.engine;

public class MatchScore {
    private static final long serialVersionUID = 1L;
    private int homeScore;
    private int awayScore;

    public MatchScore(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public int getHomeScore() { return homeScore; }
    public int getAwayScore() { return awayScore; }
    public int getGoalDiff() { return homeScore - awayScore; }

    @Override
    public String toString() {
        return homeScore + " - " + awayScore;
    }
}