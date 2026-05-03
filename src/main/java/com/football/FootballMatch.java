package com.football;
import com.abstracts.AbstractMatch;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class FootballMatch extends AbstractMatch implements Serializable {
    private static final long serialVersionUID = 1L;
    private int homeScore;
    private int awayScore;
    private static final int TOTAL_SEGMENTS = 2;
    private Random random;

    public FootballMatch(ITeam homeTeam, ITeam awayTeam) {
        super(homeTeam, awayTeam);
        this.homeScore = 0;
        this.awayScore = 0;
        this.random = new Random();
    }

    @Override
    public void simulateNextSegment() {
        if (finished) return;

        currentSegment++;

        double homeStrength = computeTeamStrength(homeTeam);
        double awayStrength = computeTeamStrength(awayTeam);
        double total = homeStrength + awayStrength;

        for (int i = 0; i < 5; i++) {
            double roll = random.nextDouble() * total;
            if (roll < homeStrength) {
                if (random.nextDouble() < 0.4) {
                    homeScore++;
                    events.add("GOAL - " + homeTeam.getName()
                            + " [" + homeScore + "-" + awayScore + "]");
                }
            } else {
                if (random.nextDouble() < 0.4) {
                    awayScore++;
                    events.add("GOAL - " + awayTeam.getName()
                            + " [" + homeScore + "-" + awayScore + "]");
                }
            }
        }

        applyInjuries(homeTeam);
        applyInjuries(awayTeam);

        updateStaminaAfterSegment(homeTeam);
        updateStaminaAfterSegment(awayTeam);

        if (currentSegment >= TOTAL_SEGMENTS) {
            finished = true;
            updateStaminaAfterMatch(homeTeam);
            updateStaminaAfterMatch(awayTeam);
        }
    }

    private double computeTeamStrength(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null || lineup.isEmpty()) return 50;

        double total = 0;
        for (IPlayer player : lineup) {
            if (player instanceof FootballPlayer) {
                FootballPlayer fp = (FootballPlayer) player;
                double effective = fp.getEffectiveOverall(fp.getPosition());
                double compatFactor = fp.getTacticCompatibility() / 100.0;
                double staminaFactor = fp.getStamina() / 100.0;
                total += effective * compatFactor * staminaFactor;
            }
        }
        return total / lineup.size();
    }

    private void applyInjuries(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null) return;
        for (IPlayer player : lineup) {
            if (player.getPosition().equals("GK")) continue; // GK exempt
            if (random.nextDouble() < 0.05) {
                int games = 1 + random.nextInt(3);
                player.injure(games);
                events.add("INJURY - " + player.getName()
                        + " (" + games + " games)");
            }
        }
    }

    private void updateStaminaAfterSegment(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null) return;
        for (IPlayer player : lineup) {
            player.updateStamina(-15);
        }
    }

    private void updateStaminaAfterMatch(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null) return;
        for (IPlayer player : lineup) {
            player.updateStamina(-10);
        }
    }

    @Override
    public Object getCurrentScore() {
        return homeScore + " - " + awayScore;
    }

    @Override
    public Object getResult() {
        if (!finished) return null;

        String winner;
        if (homeScore > awayScore) winner = homeTeam.getName();
        else if (awayScore > homeScore) winner = awayTeam.getName();
        else winner = "Draw";

        return homeTeam.getName() + " " + homeScore
                + " - " + awayScore + " " + awayTeam.getName()
                + " | Winner: " + winner;
    }

    public int getHomeScore() { return homeScore; }
    public int getAwayScore() { return awayScore; }
}