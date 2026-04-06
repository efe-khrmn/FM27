package com.football;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FootballTacticTest {

    @Test
    void testValidTacticName() {
        FootballTactic tactic = new FootballTactic("4-3-3");
        assertEquals("4-3-3", tactic.getTacticName());
    }

    @Test
    void testIsValid() {
        FootballTactic tactic = new FootballTactic("4-4-2");
        assertTrue(tactic.isValid(null));
    }

    @Test
    void testIsNotValid() {
        FootballTactic tactic = new FootballTactic("1-1-1");
        assertFalse(tactic.isValid(null));
    }

    @Test
    void testParametersNotNull() {
        FootballTactic tactic = new FootballTactic("4-3-3");
        assertNotNull(tactic.getParameters());
    }

    @Test
    void testParametersContainStyle() {
        FootballTactic tactic = new FootballTactic("4-3-3");
        assertTrue(tactic.getParameters().containsKey("style"));
        assertEquals("attack", tactic.getParameters().get("style"));
    }

    @Test
    void testAvailableNamesContainsAll() {
        FootballTactic tactic = new FootballTactic("4-4-2");
        assertTrue(tactic.getAvailableNames().contains("4-4-2"));
        assertTrue(tactic.getAvailableNames().contains("4-3-3"));
        assertTrue(tactic.getAvailableNames().contains("3-5-2"));
        assertTrue(tactic.getAvailableNames().contains("5-3-2"));
    }

    @Test
    void test442Parameters() {
        FootballTactic tactic = new FootballTactic("4-4-2");
        assertEquals("medium", tactic.getParameters().get("pressing"));
        assertEquals("crossing", tactic.getParameters().get("playMaking"));
        assertEquals("balanced", tactic.getParameters().get("style"));
    }

    @Test
    void test532Parameters() {
        FootballTactic tactic = new FootballTactic("5-3-2");
        assertEquals("low", tactic.getParameters().get("pressing"));
        assertEquals("counter attack", tactic.getParameters().get("playMaking"));
        assertEquals("defense", tactic.getParameters().get("style"));
    }
}