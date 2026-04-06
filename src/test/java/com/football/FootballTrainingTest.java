package com.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class FootballTrainingTest {

    private FootballTeam team;
    private FootballPlayer player;
    private FootballTraining training;

    @BeforeEach
    void setUp() {
        team = new FootballTeam("Test FC", "logo1", false);
        player = new FootballPlayer("Test Player", 10, 22, "ST", 65, 65);
        team.addPlayer(player);
        team.addCoach(new FootballCoach("Coach", 3, true,
                Arrays.asList("Attack")));
        training = new FootballTraining(team);
    }

    @Test
    void testTrainingTypesNotEmpty() {
        assertFalse(training.getTrainingTypes().isEmpty());
    }

    @Test
    void testTrainingTypesContainsAll() {
        assertTrue(training.getTrainingTypes().contains("Fitness"));
        assertTrue(training.getTrainingTypes().contains("Attack"));
        assertTrue(training.getTrainingTypes().contains("Defense"));
        assertTrue(training.getTrainingTypes().contains("Tactic"));
    }

    @Test
    void testAttackTrainingIncreasesAttackXP() {
        int before = player.getAttackXP();
        training.runTraining("Attack");
        assertTrue(player.getAttackXP() > before);
    }

    @Test
    void testAttackTrainingDoesNotIncreaseDefenseXP() {
        int before = player.getDefenseXP();
        training.runTraining("Attack");
        assertEquals(before, player.getDefenseXP());
    }

    @Test
    void testDefenseTrainingIncreasesDefenseXP() {
        int before = player.getDefenseXP();
        training.runTraining("Defense");
        assertTrue(player.getDefenseXP() > before);
    }

    @Test
    void testDefenseTrainingDoesNotIncreaseAttackXP() {
        int before = player.getAttackXP();
        training.runTraining("Defense");
        assertEquals(before, player.getAttackXP());
    }

    @Test
    void testFitnessTrainingIncreasesBothXP() {
        int attackBefore = player.getAttackXP();
        int defenseBefore = player.getDefenseXP();
        training.runTraining("Fitness");
        assertTrue(player.getAttackXP() > attackBefore);
        assertTrue(player.getDefenseXP() > defenseBefore);
    }

    @Test
    void testTacticTrainingIncreasesBothXP() {
        int attackBefore = player.getAttackXP();
        int defenseBefore = player.getDefenseXP();
        training.runTraining("Tactic");
        assertTrue(player.getAttackXP() > attackBefore);
        assertTrue(player.getDefenseXP() > defenseBefore);
    }

    @Test
    void testStaminaDecreasesAfterTraining() {
        int before = player.getStamina();
        training.runTraining("Attack");
        assertTrue(player.getStamina() < before);
    }

    @Test
    void testTacticCompatibilityIncreasesAfterTraining() {
        player.setTacticCompatibility(50);
        training.runTraining("Fitness");
        assertTrue(player.getTacticCompatibility() > 50);
    }

    @Test
    void testResultsNotEmptyAfterTraining() {
        training.runTraining("Attack");
        assertFalse(training.getResults().isEmpty());
    }
}