package com.engine;

import com.football.FootballTeam;
import com.interfaces.ITeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTest {

    private Schedule schedule;
    private List<ITeam> teams;

    @BeforeEach
    void setUp() {
        schedule = new Schedule();
        teams = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            teams.add(new FootballTeam("Team" + i, "logo" + i, false));
        }
        schedule.generate(teams);
    }

    @Test
    void testCorrectNumberOfWeeks() {
        // 4 teams double round robin = 6 weeks
        assertEquals(6, schedule.getTotalWeeks());
    }

    @Test
    void testEachWeekHasFixtures() {
        for (int w = 1; w <= schedule.getTotalWeeks(); w++) {
            assertFalse(schedule.getWeekFixtures(w).isEmpty());
        }
    }

    @Test
    void testEachTeamPlaysCorrectNumberOfMatches() {
        // 4 teams, each plays 6 matches (3 opponents x 2)
        for (ITeam team : teams) {
            assertEquals(6, schedule.getTeamFixtures(team).size());
        }
    }

    @Test
    void testNoFixtureForInvalidWeek() {
        assertTrue(schedule.getWeekFixtures(999).isEmpty());
    }

    @Test
    void testFixturesNotPlayedInitially() {
        for (int w = 1; w <= schedule.getTotalWeeks(); w++) {
            for (Fixture f : schedule.getWeekFixtures(w)) {
                assertFalse(f.isPlayed());
            }
        }
    }

    @Test
    void testFixtureSetResult() {
        Fixture f = schedule.getWeekFixtures(1).get(0);
        f.setResult("2-1");
        assertTrue(f.isPlayed());
        assertEquals("2-1", f.getResult());
    }
}
