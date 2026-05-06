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
    public boolean hasOvertime() { return false; }

    @Override
    public boolean allowsDraw() { return false; }

    /**
     * Volleyball scoring:
     *  3-0 / 3-1 → winner 3 pts, loser 0 pts
     *  3-2      → winner 2 pts, loser 1 pt (tie-break)
     */
    @Override
    public int[] computePoints(int homeSets, int awaySets) {
        boolean tieBreak = (homeSets == 3 && awaySets == 2) || (awaySets == 3 && homeSets == 2);
        if (homeSets > awaySets) {
            int hp = tieBreak ? 2 : 3;
            int ap = tieBreak ? 1 : 0;
            return new int[]{hp, ap, 'W', 'L'};
        } else {
            int ap = tieBreak ? 2 : 3;
            int hp = tieBreak ? 1 : 0;
            return new int[]{hp, ap, 'L', 'W'};
        }
    }

    @Override
    public Comparator<Object> getComparator() {
        return (a, b) -> {
            TeamStanding tsA = (TeamStanding) a;
            TeamStanding tsB = (TeamStanding) b;

            if (tsB.getPoints() != tsA.getPoints()) {
                return tsB.getPoints() - tsA.getPoints();
            }

            double ratioA = tsA.getConceded() == 0 ? tsA.getScored() : (double) tsA.getScored() / tsA.getConceded();
            double ratioB = tsB.getConceded() == 0 ? tsB.getScored() : (double) tsB.getScored() / tsB.getConceded();
            double ratioDiff = ratioB - ratioA;
            if (ratioDiff != 0) return ratioDiff > 0 ? 1 : -1;

            if (tsB.getScored() != tsA.getScored()) {
                return tsB.getScored() - tsA.getScored();
            }

            return new Random().nextInt(3) - 1;
        };
    }

    @Override
    public String describeTiebreakers() {
        return "1) Points  2) Sets Ratio (Won/Lost)  3) Sets Won  4) Coin Toss";
    }
}
