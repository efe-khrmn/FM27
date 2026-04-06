package com;

import com.engine.*;
import com.football.*;
import com.interfaces.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  SPORTS MANAGER - Milestone 2 Demo");
        System.out.println("========================================\n");

        // ── SPORT ──
        ISport sport = SportFactory.create("Football");
        System.out.println(">> Sport: " + sport.getSportName());
        System.out.println("   Team Size: " + sport.getTeamSize());
        System.out.println("   Max Substitutions: " + sport.getMaxSubstitutions());
        System.out.println("   Segments: " + sport.getSegmentCount() + " (" + sport.getSegmentLabel(1) + ", " + sport.getSegmentLabel(2) + ")");
        System.out.println("   Overtime: " + sport.hasOvertime());
        System.out.println("   Positions: " + sport.getPositions());
        System.out.println("   Overall Types: " + sport.getOverallTypes());
        System.out.println("   Tiebreakers: " + sport.getStandingsRules().describeTiebreakers());

        // ── PLAYERS ──
        System.out.println("\n>> Creating Players...");
        FootballPlayer gk  = new FootballPlayer("Mario Rossi", 1, 28, "GK", 55, 72);
        FootballPlayer cb1 = new FootballPlayer("Caglar Durmaz", 4, 25, "CB", 45, 78);
        FootballPlayer cb2 = new FootballPlayer("Murat Erten", 5, 27, "CB", 44, 76);
        FootballPlayer lb  = new FootballPlayer("Burak Yilmaz", 3, 23, "LB", 60, 70);
        FootballPlayer rb  = new FootballPlayer("Luca Ferrari", 2, 26, "RB", 62, 68);
        FootballPlayer cm1 = new FootballPlayer("Kevin Muller", 6, 24, "CM", 68, 65);
        FootballPlayer cm2 = new FootballPlayer("Pierre Dubois", 8, 29, "CM", 66, 64);
        FootballPlayer cam = new FootballPlayer("Marco Bianchi", 10, 22, "CAM", 74, 55);
        FootballPlayer lw  = new FootballPlayer("Kaya Oguz", 11, 21, "LW", 75, 50);
        FootballPlayer rw  = new FootballPlayer("Efe Berk", 7, 24, "RW", 73, 52);
        FootballPlayer st  = new FootballPlayer("Cem Evrendilek", 9, 26, "ST", 80, 48);

        System.out.println("   " + st.getName() + " | Pos: " + st.getPosition()
                + " | Attack: " + st.getAttackOverall()
                + " | Defense: " + st.getDefenseOverall()
                + " | Age: " + st.getAge()
                + " | Stamina: " + st.getStamina()
                + " | Compat: " + st.getTacticCompatibility());

        // ── XP PROGRESSION ──
        System.out.println("\n>> XP Progression Test...");
        System.out.println("   " + st.getName() + " Attack Overall before XP: " + st.getAttackOverall());
        st.addAttackXP(100);
        System.out.println("   " + st.getName() + " Attack Overall after 100 AttackXP: " + st.getAttackOverall());

        // ── INJURY ──
        System.out.println("\n>> Injury Test...");
        cb1.injure(2);
        System.out.println("   " + cb1.getName() + " injured: " + cb1.isInjured()
                + " | Active: " + cb1.isActive()
                + " | Games out: " + cb1.getInjuryGames());
        cb1.decrementInjury();
        System.out.println("   After 1 match: Games out: " + cb1.getInjuryGames());

        // ── TEAM ──
        System.out.println("\n>> Creating Teams...");
        FootballTeam homeTeam = new FootballTeam("Karsiyaka Seagulls", "seagulls", true);
        FootballTeam awayTeam = new FootballTeam("Balcova Idman Yurdu", "rival", false);

        FootballCoach headCoach = new FootballCoach("Roberto Mancini", 5, true,
                Arrays.asList("Attack", "Defense"));
        homeTeam.addCoach(headCoach);

        List<FootballPlayer> homePlayers = Arrays.asList(
                gk, cb1, cb2, lb, rb, cm1, cm2, cam, lw, rw, st);
        for (FootballPlayer p : homePlayers) homeTeam.addPlayer(p);

        // away team
        FootballCoach awayCoach = new FootballCoach("Ertugrul Saglam", 4, true,
                Arrays.asList("Defense"));
        awayTeam.addCoach(awayCoach);
        String[] positions = {"GK","CB","CB","LB","RB","CM","CM","CAM","LW","RW","ST"};
        for (int i = 0; i < 11; i++) {
            awayTeam.addPlayer(new FootballPlayer("Diego Lopez" + i, i+1, 25, positions[i], 65, 65));
        }

        System.out.println("   Home: " + homeTeam.getName()
                + " | Squad: " + homeTeam.getSquad().size()
                + " | Coach: " + homeTeam.getCoach().getName()
                + " (Level " + homeTeam.getCoach().getExperienceLevel() + ")");

        // ── TACTIC ──
        System.out.println("\n>> Tactic Test...");
        FootballTactic tactic = new FootballTactic("4-3-3");
        homeTeam.setTactic(tactic);
        System.out.println("   Tactic set: " + tactic.getTacticName()
                + " | Style: " + tactic.getParameters().get("style"));
        System.out.println("   All players compat reset to: "
                + homeTeam.getSquad().get(0).getTacticCompatibility());

        // ── STARTING LINEUP ──
        List<IPlayer> homeLineup = Arrays.asList(
                gk, cb1, cb2, lb, rb, cm1, cm2, cam, lw, rw, st);
        List<IPlayer> awayLineup = awayTeam.getSquad().subList(0, 11);
        homeTeam.setStartingLineup(homeLineup);
        awayTeam.setStartingLineup(awayLineup);

        // ── TRAINING ──
        System.out.println("\n>> Training Test...");
        FootballTraining training = new FootballTraining(homeTeam);
        System.out.println("   Available types: " + training.getTrainingTypes());
        int attackXPBefore = st.getAttackXP();
        training.runTraining("Attack");
        System.out.println("   After Attack training - " + st.getName()
                + " AttackXP: " + attackXPBefore + " -> " + st.getAttackXP()
                + " | Stamina: " + st.getStamina());

        // ── MATCH ──
        System.out.println("\n>> Match Simulation...");
        FootballMatch match = new FootballMatch(homeTeam, awayTeam);
        System.out.println("   " + homeTeam.getName() + " vs " + awayTeam.getName());

        match.simulateNextSegment();
        System.out.println("   Half 1 score: " + match.getCurrentScore());

        match.simulateNextSegment();
        System.out.println("   Full time: " + match.getCurrentScore());
        System.out.println("   Finished: " + match.isFinished());
        System.out.println("   Result: " + match.getResult());
        System.out.println("   Events: " + match.getEvents().size() + " events");

        // ── LEAGUE ──
        System.out.println("\n>> League Test...");
        League league = new League(sport);
        league.addTeam(homeTeam);
        league.addTeam(awayTeam);
        league.updateStandings(homeTeam, match.getHomeScore(), awayTeam, match.getAwayScore());
        league.startSeason();

        System.out.println("   Standings:");
        for (TeamStanding ts : league.getStandings()) {
            System.out.println("   " + ts.getTeam().getName()
                    + " | P: " + ts.getPoints()
                    + " | W: " + ts.getWon()
                    + " | D: " + ts.getDrawn()
                    + " | L: " + ts.getLost()
                    + " | GF: " + ts.getScored()
                    + " | GA: " + ts.getConceded()
                    + " | GD: " + ts.getGoalDiff());
        }

        // ── GAME STATE ──
        System.out.println("\n>> GameState Test...");
        GameState gs = GameState.getInstance();
        gs.newGame(sport, homeTeam);
        System.out.println("   Phase: " + gs.getPhase());
        System.out.println("   Week: " + gs.getWeek());
        System.out.println("   Managed Team: " + gs.getManagedTeam().getName());
        gs.nextPhase();
        System.out.println("   After nextPhase: " + gs.getPhase());

        System.out.println("\n========================================");
        System.out.println("  Milestone 2 - All systems operational");
        System.out.println("========================================");
    }
}