package com.football;
import com.abstracts.AbstractMatch;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;

import java.io.Serializable;
import java.util.ArrayList;
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

        // Remove injured players from active lineup before this segment
        removeInjuredFromLineup(homeTeam);
        removeInjuredFromLineup(awayTeam);

        double homeStrength = computeTeamStrength(homeTeam);
        double awayStrength = computeTeamStrength(awayTeam);
        double total = homeStrength + awayStrength;

        for (int i = 0; i < 5; i++) {
            double roll = random.nextDouble() * total;
            if (roll < homeStrength) {
                if (random.nextDouble() < 0.4) {
                    homeScore++;
                    IPlayer scorer = getRandomOutfieldPlayer(homeTeam);
                    String scorerName = scorer != null ? scorer.getName() : "Unknown";
                    events.add("⚽ GOAL - " + homeTeam.getName()
                            + " [" + scorerName + "] "
                            + homeScore + "-" + awayScore);
                }
            } else {
                if (random.nextDouble() < 0.4) {
                    awayScore++;
                    IPlayer scorer = getRandomOutfieldPlayer(awayTeam);
                    String scorerName = scorer != null ? scorer.getName() : "Unknown";
                    events.add("⚽ GOAL - " + awayTeam.getName()
                            + " [" + scorerName + "] "
                            + homeScore + "-" + awayScore);
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

    private void removeInjuredFromLineup(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null) return;
        List<IPlayer> remaining = new ArrayList<>();
        for (IPlayer p : lineup) {
            if (p.isInjured() || !p.isActive()) {
                events.add("OUT - " + p.getName() + " (injured) leaves the field");
            } else {
                remaining.add(p);
            }
        }
        if (remaining.size() != lineup.size()) {
            // bypass setStartingLineup validation by mutating list in place
            lineup.clear();
            lineup.addAll(remaining);
        }
    }

    private IPlayer getRandomOutfieldPlayer(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null || lineup.isEmpty()) return null;
        List<IPlayer> outfield = new ArrayList<>();
        for (IPlayer p : lineup) {
            if (p.isInjured() || !p.isActive()) continue;
            if (!p.getPosition().equals("GK")) outfield.add(p);
        }
        if (outfield.isEmpty()) return null;
        return outfield.get(random.nextInt(outfield.size()));
    }

    private double computeTeamStrength(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null || lineup.isEmpty()) return 50;

        double total = 0;
        int count = 0;
        for (IPlayer player : lineup) {
            if (player.isInjured() || !player.isActive()) continue; // injured players cannot contribute
            if (player instanceof FootballPlayer) {
                FootballPlayer fp = (FootballPlayer) player;
                double effective = fp.getEffectiveOverall(fp.getPosition());
                double compatFactor = fp.getTacticCompatibility() / 100.0;
                double staminaFactor = fp.getStamina() / 100.0;
                total += effective * compatFactor * staminaFactor;
                count++;
            }
        }
        if (count == 0) return 1;
        return total / count;
    }

    private void applyInjuries(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup == null) return;
        // copy to avoid concurrent modification
        List<IPlayer> snapshot = new ArrayList<>(lineup);
        for (IPlayer player : snapshot) {
            if (player.isInjured()) continue;
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