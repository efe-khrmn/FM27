package com.football;

import com.abstracts.AbstractTeam;
import com.interfaces.ICoach;
import com.interfaces.IPlayer;

import java.io.Serializable;
import java.util.List;

public class FootballTeam extends AbstractTeam implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean playerManaged;

    public FootballTeam(String name, String logoId, boolean playerManaged) {
        super(name, logoId);
        this.playerManaged = playerManaged;
    }
    public FootballTeam(String name, String logoId) {
        super(name, logoId);
    }

    public boolean isPlayerManaged() { return playerManaged; }

    public void addPlayer(IPlayer player) { squad.add(player); }

    public void addCoach(ICoach coach) { coaches.add(coach); }

    @Override
    public void setStartingLineup(List<IPlayer> players) {
        if (players.size() != 11) {
            throw new IllegalArgumentException("Starting lineup must have exactly 11 players.");
        }
        boolean hasGK = false;
        for (IPlayer p : players) {
            if (p.getPosition().equals("GK")) {
                hasGK = true;
                break;
            }
        }
        if (!hasGK) {
            throw new IllegalArgumentException("Starting lineup must include a goalkeeper.");
        }
        this.startingLineup = players;
    }
    public void setPlayerManaged(boolean managed) {
        this.playerManaged = managed;
    }
}