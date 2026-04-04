package com.football;

import com.interfaces.IStandingsRules;
import java.util.Comparator;

public class FootballStandingsRules implements IStandingsRules {
    private static final int POINTS_WIN  = 3;
    private static final int POINTS_DRAW = 1;
    private static final int POINTS_LOSS = 0;
    @Override
    public int getPointsForWin() {
        return 0;
    }

    @Override
    public int getPointsForDraw() {
        return 0;
    }

    @Override
    public int getPointsForLoss() {
        return 0;
    }

    @Override
    public boolean hasOvertime() {
        return false;
    }

    @Override
    public Comparator<Object> getComparator() {
        return null;
    }

    @Override
    public String describeTiebreakers() {
        return "";
    }
}
