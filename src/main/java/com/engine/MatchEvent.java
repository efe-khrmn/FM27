package com.engine;

import com.interfaces.IPlayer;
import com.interfaces.ITeam;

public class MatchEvent {

    public enum EventType {
        GOAL,
        INJURY,
        SUBSTITUTION
    }

    private EventType type;
    private IPlayer player;
    private ITeam team;
    private int segment;

    public MatchEvent(EventType type, IPlayer player, ITeam team, int segment) {
        this.type = type;
        this.player = player;
        this.team = team;
        this.segment = segment;
    }

    public EventType getType() { return type; }
    public IPlayer getPlayer() { return player; }
    public ITeam getTeam() { return team; }
    public int getSegment() { return segment; }

    @Override
    public String toString() {
        return "[" + type + "] " + player.getName()
                + " (" + team.getName() + ") - Segment " + segment;
    }
}

