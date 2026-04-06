package com.football;

import com.interfaces.IPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FootballMatchTest {

    private FootballTeam homeTeam;
    private FootballTeam awayTeam;
    private FootballMatch match;

    @BeforeEach
    void setUp() {
        homeTeam = new FootballTeam("Home FC", "logo1", true);
        awayTeam = new FootballTeam("Away FC", "logo2", false);

        homeTeam.addCoach(new FootballCoach("HomeCoach", 3, true,
                Arrays.asList("Attack")));
        awayTeam.addCoach(new FootballCoach("AwayCoach", 3, true,
                Arrays.asList("Defense")));

        List<IPlayer> homeLineup = createLineup(homeTeam);
        List<IPlayer> awayLineup = createLineup(awayTeam);

        homeTeam.setStartingLineup(homeLineup);
        awayTeam.setStartingLineup(awayLineup);

        match = new FootballMatch(homeTeam, awayTeam);
    }

    private List<IPlayer> createLineup(FootballTeam team) {
        String[] positions = {"GK","CB","CB","LB","RB","CM","CM","CAM","LW","RW","ST"};
        List<IPlayer> lineup = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            FootballPlayer p = new FootballPlayer(
                    "Player" + i, i + 1, 24, positions[i], 65, 65);
            team.addPlayer(p);
            lineup.add(p);
        }
        return lineup;
    }

    @Test
    void testInitialState() {
        assertEquals(0, match.getCurrentSegment());
        assertFalse(match.isFinished());
        assertNotNull(match.getCurrentScore());
    }

    @Test
    void testSimulateFirstSegment() {
        match.simulateNextSegment();
        assertEquals(1, match.getCurrentSegment());
        assertFalse(match.isFinished());
    }

    @Test
    void testSimulateBothSegments() {
        match.simulateNextSegment();
        match.simulateNextSegment();
        assertEquals(2, match.getCurrentSegment());
        assertTrue(match.isFinished());
    }

    @Test
    void testResultAfterFinish() {
        match.simulateNextSegment();
        match.simulateNextSegment();
        assertNotNull(match.getResult());
    }

    @Test
    void testResultNullBeforeFinish() {
        match.simulateNextSegment();
        assertNull(match.getResult());
    }

    @Test
    void testNoMoreSimulationAfterFinished() {
        match.simulateNextSegment();
        match.simulateNextSegment();
        match.simulateNextSegment(); // should be ignored
        assertEquals(2, match.getCurrentSegment());
    }

    @Test
    void testStaminaDecreasesAfterSegment() {
        int initialStamina = homeTeam.getStartingLineup().get(0).getStamina();
        match.simulateNextSegment();
        int afterStamina = homeTeam.getStartingLineup().get(0).getStamina();
        assertTrue(afterStamina < initialStamina);
    }

    @Test
    void testGKNotInjured() {
        // run many matches to increase injury probability
        for (int i = 0; i < 50; i++) {
            FootballMatch m = new FootballMatch(homeTeam, awayTeam);
            m.simulateNextSegment();
            m.simulateNextSegment();
        }
        IPlayer gk = homeTeam.getStartingLineup().get(0);
        assertEquals("GK", gk.getPosition());
        assertFalse(gk.isInjured());
    }

    @Test
    void testEventsNotEmpty() {
        match.simulateNextSegment();
        match.simulateNextSegment();
        assertNotNull(match.getEvents());
    }

    @Test
    void testHomeAndAwayTeams() {
        assertEquals(homeTeam, match.getHomeTeam());
        assertEquals(awayTeam, match.getAwayTeam());
    }
}