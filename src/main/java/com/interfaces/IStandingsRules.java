package com.interfaces;
import java.util.Comparator;

public interface IStandingsRules {
    int getPointsForWin();
    int getPointsForDraw();
    int getPointsForLoss();
    boolean hasOvertime();
    Comparator<Object> getComparator();
    String describeTiebreakers();

    /**
     * Returns [homePoints, awayPoints, homeResult, awayResult]
     * where result chars 'W','D','L' encoded as int.
     */
    default int[] computePoints(int homeScore, int awayScore) {
        if (homeScore > awayScore) {
            return new int[]{getPointsForWin(), getPointsForLoss(), 'W', 'L'};
        } else if (awayScore > homeScore) {
            return new int[]{getPointsForLoss(), getPointsForWin(), 'L', 'W'};
        } else {
            return new int[]{getPointsForDraw(), getPointsForDraw(), 'D', 'D'};
        }
    }

    default boolean allowsDraw() { return true; }
}
