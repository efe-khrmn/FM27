package com.interfaces;
import java.util.Comparator;

public interface IStandingsRules {
    int getPointsForWin();
    int getPointsForDraw();
    int getPointsForLoss();
    boolean hasOvertime();
    Comparator<Object> getComparator();
    String describeTiebreakers();

    // Score-aware points (used by sports like volleyball where the margin matters).
    // Default implementations fall back to flat win/loss points.
    default int getPointsForWin(int winnerScore, int loserScore) {
        return getPointsForWin();
    }
    default int getPointsForLoss(int winnerScore, int loserScore) {
        return getPointsForLoss();
    }
}

