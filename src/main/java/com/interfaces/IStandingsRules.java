package com.interfaces;
import java.util.Comparator;

public interface IStandingsRules {
    int getPointsForWin();
    int getPointsForDraw();
    int getPointsForLoss();
    boolean hasOvertime();
    Comparator<Object> getComparator();
    String describeTiebreakers();
}