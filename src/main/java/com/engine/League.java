package com.engine;

import com.interfaces.ISport;
import com.interfaces.ITeam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class League implements Serializable {
    private static final long serialVersionUID = 1L;
    private ISport sport;
    private List<ITeam> teams;
    private Schedule schedule;
    private List<TeamStanding> standings;

    public League(ISport sport) {
        this.sport = sport;
        this.teams = new ArrayList<>();
        this.schedule = new Schedule();
        this.standings = new ArrayList<>();
    }

    public void addTeam(ITeam team) {
        teams.add(team);
        standings.add(new TeamStanding(team));
    }

    public void startSeason() {
        schedule.generate(teams);
    }

    public void updateStandings(ITeam homeTeam, int homeGoals,
                                ITeam awayTeam, int awayGoals) {
        int[] pts = sport.getStandingsRules().computePoints(homeGoals, awayGoals);
        int homePoints = pts[0];
        int awayPoints = pts[1];
        char homeRes = (char) pts[2];
        char awayRes = (char) pts[3];

        for (TeamStanding ts : standings) {
            if (ts.getTeam().equals(homeTeam)) {
                ts.update(homeGoals, awayGoals, homePoints, homeRes);
            } else if (ts.getTeam().equals(awayTeam)) {
                ts.update(awayGoals, homeGoals, awayPoints, awayRes);
            }
        }
    }

    public List<TeamStanding> getStandings() {
        standings.sort(sport.getStandingsRules().getComparator());
        return standings;
    }

    public boolean isSeasonOver() {
        int totalWeeks = schedule.getTotalWeeks();
        for (List<Fixture> weekFixtures : getAllFixtures()) {
            for (Fixture f : weekFixtures) {
                if (!f.isPlayed()) return false;
            }
        }
        return true;
    }

    private List<List<Fixture>> getAllFixtures() {
        List<List<Fixture>> all = new ArrayList<>();
        for (int w = 1; w <= schedule.getTotalWeeks(); w++) {
            all.add(schedule.getWeekFixtures(w));
        }
        return all;
    }

    public List<ITeam> getTeams() { return teams; }
    public Schedule getSchedule() { return schedule; }
    public ISport getSport() { return sport; }
}
