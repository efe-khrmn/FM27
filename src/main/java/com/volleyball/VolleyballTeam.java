package com.volleyball;

import com.abstracts.AbstractTeam;
import com.interfaces.ICoach;
import com.interfaces.IPlayer;
import com.interfaces.ITactic;

import java.io.Serializable;
import java.util.List;

public class VolleyballTeam extends AbstractTeam implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean playerManaged;

    public VolleyballTeam(String name, String logoId, boolean playerManaged) {
        super(name, logoId);
        this.playerManaged = playerManaged;
    }

    public boolean isPlayerManaged() { return playerManaged; }

    public void addPlayer(IPlayer player) { squad.add(player); }

    public void addCoach(ICoach coach) { coaches.add(coach); }

    @Override
    public void setStartingLineup(List<IPlayer> players) {
        if (players.size() != 6) {
            throw new IllegalArgumentException("Starting lineup must have exactly 6 players.");
        }
        boolean hasLibero = false;
        boolean hasSetter = false;
        for (IPlayer p : players) {
            if (p.getPosition().equals("L")) hasLibero = true;
            if (p.getPosition().equals("S")) hasSetter = true;
        }
        if (!hasLibero) {
            throw new IllegalArgumentException("Starting lineup must include a Libero.");
        }
        if (!hasSetter) {
            throw new IllegalArgumentException("Starting lineup must include a Setter.");
        }
        this.startingLineup = players;
    }
    public void setPlayerManaged(boolean managed) {
        this.playerManaged = managed;
    }
}