package com.engine;

import com.football.FootballCoach;
import com.football.FootballPlayer;
import com.football.FootballSport;
import com.football.FootballTeam;
import com.interfaces.ISport;
import com.interfaces.ITeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LeagueTest {

    private League league;
    private FootballTeam team1;
    private FootballTeam team2;
    private FootballTeam team3;
    private FootballTeam team4;

    @BeforeEach
    void setUp() {
        ISport sport = new FootballSport();
        league = new League(sport);

        team1 = new FootballTeam("Team A", "logo1", true);
        team2 = new FootballTeam("Team B", "logo2", false);
        team3 = new FootballTeam("Team C", "logo3", false);
        team4 = new FootballTeam("Team D", "logo4", false);

        league.addTeam(team1);
        league.addTeam(team2);
        league.addTeam(team3);
        league.addTeam(team4);
    }

    @Test
    void testTeamCount() {
        assertEquals(4, league.getTeams().size());
    }

    @Test
    void testStandingsSize() {
        assertEquals(4, league.getStandings().size());
    }

    @Test
    void testUpdateStandingsWin() {
        league.updateStandings(team1, 2, team2, 0);
        TeamStanding ts1 = findStanding(team1);
        TeamStanding ts2 = findStanding(team2);
        assertEquals(3, ts1.getPoints());
        assertEquals(0, ts2.getPoints());
        assertEquals(1, ts1.getWon());
        assertEquals(1, ts2.getLost());
    }

    @Test
    void testUpdateStandingsDraw() {
        league.updateStandings(team1, 1, team2, 1);
        TeamStanding ts1 = findStanding(team1);
        TeamStanding ts2 = findStanding(team2);
        assertEquals(1, ts1.getPoints());
        assertEquals(1, ts2.getPoints());
        assertEquals(1, ts1.getDrawn());
        assertEquals(1, ts2.getDrawn());
    }

    @Test
    void testUpdateStandingsGoals() {
        league.updateStandings(team1, 3, team2, 1);
        TeamStanding ts1 = findStanding(team1);
        TeamStanding ts2 = findStanding(team2);
        assertEquals(3, ts1.getScored());
        assertEquals(1, ts1.getConceded());
        assertEquals(1, ts2.getScored());
        assertEquals(3, ts2.getConceded());
    }

    @Test
    void testStandingsOrdered() {
        league.updateStandings(team1, 2, team2, 0);
        league.updateStandings(team3, 1, team4, 0);
        List<TeamStanding> standings = league.getStandings();
        assertTrue(standings.get(0).getPoints() >= standings.get(1).getPoints());
    }

    @Test
    void testSeasonNotOverAtStart() {
        league.startSeason();
        assertFalse(league.isSeasonOver());
    }

    private TeamStanding findStanding(ITeam team) {
        for (TeamStanding ts : league.getStandings()) {
            if (ts.getTeam().equals(team)) return ts;
        }
        return null;
    }
}