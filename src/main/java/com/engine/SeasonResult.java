package com.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class SeasonResult {
    private final List<TeamStanding> finalStandings;

    public SeasonResult(List<TeamStanding> finalStandings) {
        List<TeamStanding> snapshot = new ArrayList<>(finalStandings);


        Collections.sort(snapshot, new Comparator<TeamStanding>() {
            @Override
            public int compare(TeamStanding t1, TeamStanding t2) {
                if (t1.getPoints() != t2.getPoints()) {
                    return Integer.compare(t2.getPoints(), t1.getPoints());
                }
                return Integer.compare(t2.getGoalDiff(), t1.getGoalDiff());
            }
        });

        this.finalStandings = Collections.unmodifiableList(snapshot);
    }

    public List<TeamStanding> getFinalStandings() {
        return finalStandings;
    }

    public TeamStanding getChampion() {
        return finalStandings.isEmpty() ? null : finalStandings.get(0);
    }
}