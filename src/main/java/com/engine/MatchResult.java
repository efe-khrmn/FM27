package com.engine;
import com.interfaces.ITeam;

import java.util.List;

public class MatchResult {
    private static final long serialVersionUID = 1L;
    private MatchScore score;
    private ITeam winner;
    private ITeam homeTeam;
    private ITeam awayTeam;
    private List<String> injuredPlayers;
    private int pointsHome;
    private int pointsAway;

    public MatchResult(MatchScore score, ITeam winner, ITeam homeTeam, ITeam awayTeam,
                       List<String> injuredPlayers, int pointsHome, int pointsAway) {
        this.score = score;
        this.winner = winner;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.injuredPlayers = injuredPlayers;
        this.pointsHome = pointsHome;
        this.pointsAway = pointsAway;
    }

    public MatchScore getScore() { return score; }
    public ITeam getWinner() { return winner; }
    public ITeam getHomeTeam() { return homeTeam; }
    public ITeam getAwayTeam() { return awayTeam; }
    public List<String> getInjuredPlayers() { return injuredPlayers; }
    public int getPointsHome() { return pointsHome; }
    public int getPointsAway() { return pointsAway; }
    public boolean isDraw() { return winner == null; }

    @Override
    public String toString() {
        return homeTeam.getName() + " " + score + " " + awayTeam.getName()
                + " | Winner: " + (isDraw() ? "Draw" : winner.getName());
    }
}
