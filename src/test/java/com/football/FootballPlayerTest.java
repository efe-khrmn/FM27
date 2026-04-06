package com.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FootballPlayerTest {

    private FootballPlayer player;

    @BeforeEach
    void setUp() {
        player = new FootballPlayer("John Doe", 10, 22, "ST", 65, 65);
    }

    @Test
    void testInitialValues() {
        assertEquals("John Doe", player.getName());
        assertEquals(10, player.getNumber());
        assertEquals(22, player.getAge());
        assertEquals("ST", player.getPosition());
        assertEquals(65, player.getAttackOverall());
        assertEquals(65, player.getDefenseOverall());
        assertEquals(0, player.getAttackXP());
        assertEquals(0, player.getDefenseXP());
        assertEquals(100, player.getStamina());
        assertEquals(50, player.getTacticCompatibility());
        assertFalse(player.isInjured());
        assertTrue(player.isActive());
    }

    @Test
    void testAttackXPProgressionUnder70() {
        // under 70, threshold is 100
        int initialAttack = player.getAttackOverall();
        player.addAttackXP(100);
        assertEquals(initialAttack + 1, player.getAttackOverall());
    }

    @Test
    void testDefenseXPProgressionUnder70() {
        int initialDefense = player.getDefenseOverall();
        player.addDefenseXP(100);
        assertEquals(initialDefense + 1, player.getDefenseOverall());
    }

    @Test
    void testAttackXPDoesNotAffectDefense() {
        int initialDefense = player.getDefenseOverall();
        player.addAttackXP(200);
        assertEquals(initialDefense, player.getDefenseOverall());
    }

    @Test
    void testDefenseXPDoesNotAffectAttack() {
        int initialAttack = player.getAttackOverall();
        player.addDefenseXP(200);
        assertEquals(initialAttack, player.getAttackOverall());
    }

    @Test
    void testXPThreshold70to80() {
        // set attack to 70
        FootballPlayer p = new FootballPlayer("Test", 1, 25, "ST", 70, 50);
        int initial = p.getAttackOverall();
        p.addAttackXP(149); // not enough
        assertEquals(initial, p.getAttackOverall());
        p.addAttackXP(1); // now 150, should progress
        assertEquals(initial + 1, p.getAttackOverall());
    }

    @Test
    void testXPThreshold80to90() {
        FootballPlayer p = new FootballPlayer("Test", 1, 25, "ST", 80, 50);
        int initial = p.getAttackOverall();
        p.addAttackXP(199);
        assertEquals(initial, p.getAttackOverall());
        p.addAttackXP(1);
        assertEquals(initial + 1, p.getAttackOverall());
    }

    @Test
    void testXPThreshold90to99() {
        FootballPlayer p = new FootballPlayer("Test", 1, 25, "ST", 90, 50);
        int initial = p.getAttackOverall();
        p.addAttackXP(299);
        assertEquals(initial, p.getAttackOverall());
        p.addAttackXP(1);
        assertEquals(initial + 1, p.getAttackOverall());
    }

    @Test
    void testOverallCappedAt99() {
        FootballPlayer p = new FootballPlayer("Test", 1, 25, "ST", 99, 50);
        p.addAttackXP(1000);
        assertEquals(99, p.getAttackOverall());
    }

    @Test
    void testStaminaDecrease() {
        player.updateStamina(-20);
        assertEquals(80, player.getStamina());
    }

    @Test
    void testStaminaNotBelowZero() {
        player.updateStamina(-200);
        assertEquals(0, player.getStamina());
    }

    @Test
    void testStaminaNotAbove100() {
        player.updateStamina(50);
        assertEquals(100, player.getStamina());
    }

    @Test
    void testInjury() {
        player.injure(3);
        assertTrue(player.isInjured());
        assertFalse(player.isActive());
        assertEquals(3, player.getInjuryGames());
    }

    @Test
    void testDecrementsInjury() {
        player.injure(2);
        player.decrementInjury();
        assertEquals(1, player.getInjuryGames());
        player.decrementInjury();
        assertEquals(0, player.getInjuryGames());
        assertFalse(player.isInjured());
        assertTrue(player.isActive());
    }

    @Test
    void testTacticCompatibility() {
        player.setTacticCompatibility(80);
        assertEquals(80, player.getTacticCompatibility());
    }

    @Test
    void testTacticCompatibilityNotAbove100() {
        player.setTacticCompatibility(150);
        assertEquals(100, player.getTacticCompatibility());
    }

    @Test
    void testTacticCompatibilityNotBelow0() {
        player.setTacticCompatibility(-10);
        assertEquals(0, player.getTacticCompatibility());
    }

    @Test
    void testEffectiveOverallSamePosition() {
        double effective = player.getEffectiveOverall("ST");
        double expected = (65 + 65) / 2.0;
        assertEquals(expected, effective, 0.01);
    }

    @Test
    void testEffectiveOverallSameGroup() {
        // ST and LW are both attack group
        double effective = player.getEffectiveOverall("LW");
        double expected = (65 + 65) / 2.0 * 0.95;
        assertEquals(expected, effective, 0.01);
    }

    @Test
    void testEffectiveOverallAdjacentGroup() {
        // ST (attack) to CM (midfield) — adjacent
        double effective = player.getEffectiveOverall("CM");
        double expected = (65 + 65) / 2.0 * 0.90;
        assertEquals(expected, effective, 0.01);
    }

    @Test
    void testEffectiveOverallDifferentGroup() {
        // ST (attack) to GK (goalkeeper) — completely different
        double effective = player.getEffectiveOverall("GK");
        double expected = (65 + 65) / 2.0 * 0.70;
        assertEquals(expected, effective, 0.01);
    }
}