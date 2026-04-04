package com.football;

import com.engine.TeamStanding;
import com.interfaces.IStandingsRules;

import java.util.Comparator;
import java.util.Random;

public class FootballStandingsRules implements IStandingsRules {
    public String düzeltme;
    @Override
    public int getPointsForWin() { return 3; }

    @Override
    public int getPointsForDraw() { return 1; }

    @Override
    public int getPointsForLoss() { return 0; }

    @Override
    public boolean hasOvertime() { return false; }

    @Override
    public Comparator<Object> getComparator() {
        return (a, b) -> {
            TeamStanding tsA = (TeamStanding) a;
            TeamStanding tsB = (TeamStanding) b;

            if (tsB.getPoints() != tsA.getPoints()) {
                return tsB.getPoints() - tsA.getPoints();
            }

            double avgDiff = tsB.getAverage() - tsA.getAverage();
            if (avgDiff != 0) {
                return avgDiff > 0 ? 1 : -1;
            }

            // 3) Goals scored
            if (tsB.getScored() != tsA.getScored()) {
                return tsB.getScored() - tsA.getScored();
            }

            if (tsB.getGoalDiff() != tsA.getGoalDiff()) {
                return tsB.getGoalDiff() - tsA.getGoalDiff();
            }

            return new Random().nextInt(3) - 1;
        };
    }

    @Override
    public String describeTiebreakers() {
        return "1) Points  2) Goal Average (For/Against)  " +
                "3) Goals Scored  4) Goal Difference  5) Coin Toss";
    }
}