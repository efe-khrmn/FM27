package com.abstracts;

import com.interfaces.ICoach;
import com.interfaces.IPlayer;
import com.interfaces.ITeam;
import com.interfaces.ITactic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTeam implements ITeam, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected String logoId;
    protected List<IPlayer> squad;
    protected List<ICoach> coaches;
    protected ITactic tactic;
    protected List<IPlayer> startingLineup;

    public AbstractTeam(String name, String logoId) {
        this.name = name;
        this.logoId = logoId;
        this.squad = new ArrayList<>();
        this.coaches = new ArrayList<>();
        this.startingLineup = new ArrayList<>();
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getLogoId() { return logoId; }

    @Override
    public List<IPlayer> getSquad() { return squad; }

    @Override
    public List<IPlayer> getAvailablePlayers() {
        List<IPlayer> available = new ArrayList<>();
        for (IPlayer player : squad) {
            if (player.isActive()) {
                available.add(player);
            }
        }
        return available;
    }

    @Override
    public ICoach getCoach() {
        for (ICoach coach : coaches) {
            if (coach.isHeadCoach()) {
                return coach;
            }
        }
        return null;
    }

    @Override
    public List<ICoach> getCoaches() { return coaches; }

    @Override
    public ITactic getTactic() { return tactic; }

    @Override
    public void setTactic(ITactic tactic) {
        this.tactic = tactic;
        // reset tactic compatibility for all players
        for (IPlayer player : squad) {
            player.setTacticCompatibility(50);
        }
    }

    @Override
    public List<IPlayer> getStartingLineup() { return startingLineup; }

    @Override
    public void setStartingLineup(List<IPlayer> players) {
        this.startingLineup = players;
    }
}