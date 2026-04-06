package com.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.interfaces.IPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FootballTeamTest {

    private FootballTeam team;
    private FootballPlayer gk;
    private List<FootballPlayer> outfieldPlayers;

    @BeforeEach
    void setUp() {
        team = new FootballTeam("Test FC", "logo1", false);

        gk = new FootballPlayer("GK Player", 1, 25, "GK", 50, 70);
        team.addPlayer(gk);

        outfieldPlayers = new ArrayList<>();
        String[] positions = {"CB","CB","LB","RB","CM","CM","CAM","LW","RW","ST"};
        for (int i = 0; i < 10; i++) {
            FootballPlayer p = new FootballPlayer(
                    "Player" + i, i + 2, 24, positions[i], 60, 60);
            outfieldPlayers.add(p);
            team.addPlayer(p);
        }

        FootballCoach coach = new FootballCoach("Coach", 3, true,
                Arrays.asList("Attack"));
        team.addCoach(coach);
    }

    @Test
    void testTeamName() {
        assertEquals("Test FC", team.getName());
    }

    @Test
    void testSquadSize() {
        assertEquals(11, team.getSquad().size());
    }

    @Test
    void testGetHeadCoach() {
        assertNotNull(team.getCoach());
        assertTrue(team.getCoach().isHeadCoach());
    }

    @Test
    void testSetValidStartingLineup() {
        List<IPlayer> lineup = new ArrayList<>();
        lineup.add(gk);
        for (FootballPlayer p : outfieldPlayers) lineup.add(p);
        assertDoesNotThrow(() -> team.setStartingLineup(lineup));
    }

    @Test
    void testSetStartingLineupWithoutGK() {
        List<IPlayer> lineup = new ArrayList<>();
        for (FootballPlayer p : outfieldPlayers) lineup.add(p);
        lineup.add(new FootballPlayer("Extra", 99, 25, "ST", 60, 60));
        assertThrows(IllegalArgumentException.class,
                () -> team.setStartingLineup(lineup));
    }

    @Test
    void testSetStartingLineupWrongSize() {
        List<IPlayer> lineup = new ArrayList<>();
        lineup.add(gk);
        assertThrows(IllegalArgumentException.class,
                () -> team.setStartingLineup(lineup));
    }

    @Test
    void testSetTacticResetsCompatibility() {
        // first set compatibility to 80 for all
        for (IPlayer p : team.getSquad()) {
            p.setTacticCompatibility(80);
        }
        FootballTactic tactic = new FootballTactic("4-3-3");
        team.setTactic(tactic);
        for (IPlayer p : team.getSquad()) {
            assertEquals(50, p.getTacticCompatibility());
        }
    }

    @Test
    void testAvailablePlayers() {
        gk.injure(2);
        List<IPlayer> available = team.getAvailablePlayers();
        assertFalse(available.contains(gk));
        assertEquals(10, available.size());
    }
}