package com.volleyball;

import com.engine.TeamStanding;
import com.interfaces.IStandingsRules;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;

public class VolleyballStandingsRules implements IStandingsRules, Serializable {
    private static final long serialVersionUID = 1L;
    @Override
    public int getPointsForWin() { return 3; }  // 3-0 or 3-1 win

    @Override
    public int getPointsForDraw() { return 0; } // no draws in volleyball

    @Override
    public int getPointsForLoss() { return 0; }

    @Override
    public int getPointsForWin(int winnerSets, int loserSets) {
        // 3-2 (tie-break) win → 2 points; 3-0 / 3-1 → 3 points
        return loserSets >= 2 ? 2 : 3;
    }

    @Override
    public int getPointsForLoss(int winnerSets, int loserSets) {
        // Loser of a tie-break (lost 2-3) gets 1 point
        return loserSets >= 2 ? 1 : 0;
    }

    @Override
    public boolean hasOvertime() { return false; } // sets handle tiebreaking

    @Override
    public Comparator<Object> getComparator() {
        return (a, b) -> {
            TeamStanding tsA = (TeamStanding) a;
            TeamStanding tsB = (TeamStanding) b;

            // 1) Points
            if (tsB.getPoints() != tsA.getPoints()) {
                return tsB.getPoints() - tsA.getPoints();
            }

            // 2) Sets ratio (scored / conceded)
            double ratioA = tsA.getConceded() == 0 ? tsA.getScored() : (double) tsA.getScored() / tsA.getConceded();
            double ratioB = tsB.getConceded() == 0 ? tsB.getScored() : (double) tsB.getScored() / tsB.getConceded();
            double ratioDiff = ratioB - ratioA;
            if (ratioDiff != 0) return ratioDiff > 0 ? 1 : -1;

            // 3) Sets won
            if (tsB.getScored() != tsA.getScored()) {
                return tsB.getScored() - tsA.getScored();
            }

            // 4) Coin toss
            return new Random().nextInt(3) - 1;
        };
    }

    @Override
    public String describeTiebreakers() {
        return "1) Points  2) Sets Ratio (Won/Lost)  3) Sets Won  4) Coin Toss";
    }
}
