package com.volleyball;

import com.abstracts.AbstractMatch;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class VolleyballMatch extends AbstractMatch implements Serializable {
    private static final long serialVersionUID = 1L;
    private int homeSetsWon;
    private int awaySetsWon;
    private int homeCurrentSetPoints;
    private int awayCurrentSetPoints;
    private static final int SETS_TO_WIN = 3;
    private static final int POINTS_PER_SET = 25;
    private static final int POINTS_FINAL_SET = 15;
    private Random random;

    public VolleyballMatch(ITeam homeTeam, ITeam awayTeam) {
        super(homeTeam, awayTeam);
        this.homeSetsWon = 0;
        this.awaySetsWon = 0;
        this.homeCurrentSetPoints = 0;
        this.awayCurrentSetPoints = 0;
        this.random = new Random();
    }

    @Override
    public void simulateNextSegment() {
        if (finished) return;

        currentSegment++;
        homeCurrentSetPoints = 0;
        awayCurrentSetPoints = 0;

        // 5th set has 15 points, others 25
        int pointsNeeded = (currentSegment == 5) ? POINTS_FINAL_SET : POINTS_PER_SET;

        double homeStrength = computeTeamStrength(homeTeam);
        double awayStrength = computeTeamStrength(awayTeam);
        double total = homeStrength + awayStrength;

        // simulate rallies until one team reaches pointsNeeded with 2 point lead
        while (true) {
            double roll = random.nextDouble() * total;
            if (roll < homeStrength) {
                homeCurrentSetPoints++;
            } else {
                awayCurrentSetPoints++;
            }

            if (homeCurrentSetPoints >= pointsNeeded &&
                    homeCurrentSetPoints - awayCurrentSetPoints >= 2) {
                homeSetsWon++;
                events.add("SET " + currentSegment + " - " + homeTeam.getName()
                        + " wins " + homeCurrentSetPoints + "-" + awayCurrentSetPoints);
                break;
            }
            if (awayCurrentSetPoints >= pointsNeeded &&
                    awayCurrentSetPoints - homeCurrentSetPoints >= 2) {
                awaySetsWon++;
                events.add("SET " + currentSegment + " - " + awayTeam.getName()
                        + " wins " + awayCurrentSetPoints + "-" + homeCurrentSetPoints);
                break;
            }
        }

        // update stamina after each set
        updateStaminaAfterSet(homeTeam);
        updateStaminaAfterSet(awayTeam);

        // check if match is over
        if (homeSetsWon >= SETS_TO_WIN || awaySetsWon >= SETS_TO_WIN) {
            finished = true;
        }
    }

    private double computeTeamStrength(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null || lineup.isEmpty()) return 50;

        double total = 0;
        int count = 0;
        for (IPlayer player : lineup) {
            if (player.isInjured() || !player.isActive()) continue; // injured cannot play
            if (player instanceof VolleyballPlayer) {
                VolleyballPlayer vp = (VolleyballPlayer) player;
                double effective = vp.getEffectiveOverall(player.getPosition());
                double compatFactor = player.getTacticCompatibility() / 100.0;
                double staminaFactor = player.getStamina() / 100.0;
                total += effective * compatFactor * staminaFactor;
                count++;
            }
        }
        if (count == 0) return 1;
        return total / count;
    }

    private void updateStaminaAfterSet(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null) return;
        for (IPlayer player : lineup) {
            player.updateStamina(-10);
        }
    }

    @Override
    public Object getCurrentScore() {
        return homeSetsWon + " - " + awaySetsWon + " sets";
    }

    @Override
    public Object getResult() {
        if (!finished) return null;

        String winner;
        if (homeSetsWon > awaySetsWon) winner = homeTeam.getName();
        else winner = awayTeam.getName();

        return homeTeam.getName() + " " + homeSetsWon
                + " - " + awaySetsWon + " " + awayTeam.getName()
                + " | Winner: " + winner;
    }

    @Override
    public int getHomeScore() {
        return 0;
    }

    @Override
    public int getAwayScore() {
        return 0;
    }

    public int getHomeSetsWon() { return homeSetsWon; }
    public int getAwaySetsWon() { return awaySetsWon; }
}
