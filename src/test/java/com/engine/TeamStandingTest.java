package com.engine;

import com.football.FootballTeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TeamStandingTest {

    private TeamStanding standing;

    @BeforeEach
    void setUp() {
        standing = new TeamStanding(new FootballTeam("Test FC", "logo", false));
    }

    @Test
    void testInitialValues() {
        assertEquals(0, standing.getPlayed());
        assertEquals(0, standing.getWon());
        assertEquals(0, standing.getDrawn());
        assertEquals(0, standing.getLost());
        assertEquals(0, standing.getScored());
        assertEquals(0, standing.getConceded());
        assertEquals(0, standing.getPoints());
        assertEquals(0, standing.getGoalDiff());
    }

    @Test
    void testUpdateWin() {
        standing.update(3, 1, 3);
        assertEquals(1, standing.getPlayed());
        assertEquals(1, standing.getWon());
        assertEquals(0, standing.getDrawn());
        assertEquals(0, standing.getLost());
        assertEquals(3, standing.getPoints());
        assertEquals(3, standing.getScored());
        assertEquals(1, standing.getConceded());
        assertEquals(2, standing.getGoalDiff());
    }

    @Test
    void testUpdateDraw() {
        standing.update(1, 1, 1);
        assertEquals(1, standing.getDrawn());
        assertEquals(1, standing.getPoints());
        assertEquals(0, standing.getGoalDiff());
    }

    @Test
    void testUpdateLoss() {
        standing.update(0, 2, 0);
        assertEquals(1, standing.getLost());
        assertEquals(0, standing.getPoints());
        assertEquals(-2, standing.getGoalDiff());
    }

    @Test
    void testMultipleUpdates() {
        standing.update(2, 1, 3);
        standing.update(1, 1, 1);
        standing.update(0, 1, 0);
        assertEquals(3, standing.getPlayed());
        assertEquals(1, standing.getWon());
        assertEquals(1, standing.getDrawn());
        assertEquals(1, standing.getLost());
        assertEquals(4, standing.getPoints());
    }

    @Test
    void testAverage() {
        standing.update(4, 2, 3);
        assertEquals(2.0, standing.getAverage(), 0.01);
    }
}