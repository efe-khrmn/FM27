package com.football;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FootballCoachTest {

    @Test
    void testHeadCoach() {
        FootballCoach coach = new FootballCoach("Alex", 4, true,
                Arrays.asList("Attack", "Defense"));
        assertTrue(coach.isHeadCoach());
        assertEquals("Alex", coach.getName());
        assertEquals(4, coach.getExperienceLevel());
    }

    @Test
    void testAssistantCoach() {
        FootballCoach coach = new FootballCoach("Bob", 2, false,
                Arrays.asList("Fitness"));
        assertFalse(coach.isHeadCoach());
    }

    @Test
    void testExperienceLevelCappedAt5() {
        FootballCoach coach = new FootballCoach("Test", 10, true,
                Arrays.asList("Attack"));
        assertEquals(5, coach.getExperienceLevel());
    }

    @Test
    void testExperienceLevelMinimum1() {
        FootballCoach coach = new FootballCoach("Test", 0, true,
                Arrays.asList("Attack"));
        assertEquals(1, coach.getExperienceLevel());
    }

    @Test
    void testSpecializations() {
        List<String> specs = Arrays.asList("Attack", "Defense");
        FootballCoach coach = new FootballCoach("Test", 3, true, specs);
        assertEquals(2, coach.getSpecializations().size());
        assertTrue(coach.getSpecializations().contains("Attack"));
    }
}