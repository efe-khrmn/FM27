package com.engine;

import com.interfaces.ITeam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<Integer, List<Fixture>> fixtures;

    public Schedule() {
        this.fixtures = new HashMap<>();
    }

    public void generate(List<ITeam> teams) {
        fixtures.clear();
        int teamCount = teams.size();
        int weeks = (teamCount - 1) * 2;

        // double round robin
        List<ITeam> list = new ArrayList<>(teams);

        for (int round = 0; round < teamCount - 1; round++) {
            int week = round + 1;
            fixtures.put(week, new ArrayList<>());

            for (int i = 0; i < teamCount / 2; i++) {
                ITeam home = list.get(i);
                ITeam away = list.get(teamCount - 1 - i);
                fixtures.get(week).add(new Fixture(home, away, week));
            }

            // rotate list except first element
            ITeam last = list.remove(teamCount - 1);
            list.add(1, last);
        }

        // second half — reverse home/away
        for (int round = 0; round < teamCount - 1; round++) {
            int week = round + teamCount;
            fixtures.put(week, new ArrayList<>());

            List<Fixture> firstHalf = fixtures.get(round + 1);
            for (Fixture f : firstHalf) {
                fixtures.get(week).add(new Fixture(f.getAwayTeam(), f.getHomeTeam(), week));
            }
        }
    }

    public List<Fixture> getWeekFixtures(int week) {
        List<Fixture> result = fixtures.get(week);
        if (result == null) return new ArrayList<>();
        return result;
    }

    public List<Fixture> getTeamFixtures(ITeam team) {
        List<Fixture> result = new ArrayList<>();
        for (List<Fixture> weekFixtures : fixtures.values()) {
            for (Fixture f : weekFixtures) {
                if (f.getHomeTeam().equals(team) || f.getAwayTeam().equals(team)) {
                    result.add(f);
                }
            }
        }
        return result;
    }

    public int getTotalWeeks() { return fixtures.size(); }
}