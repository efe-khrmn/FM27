package com.abstracts;

import com.interfaces.*;
import java.util.*;

public abstract class AbstractMatch implements IMatch {
    private static final long serialVersionUID = 1L;
    protected ITeam homeTeam;
    protected ITeam awayTeam;
    protected int currentSegment;
    protected boolean finished;
    protected List<Object> events;

    public AbstractMatch(ITeam homeTeam, ITeam awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.currentSegment = 0;
        this.finished = false;
        this.events = new ArrayList<>();
    }

    @Override
    public ITeam getHomeTeam() {
        return homeTeam;
    }

    @Override
    public ITeam getAwayTeam() {
        return awayTeam;
    }

    @Override
    public int getCurrentSegment() {
        return currentSegment;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public List<Object> getEvents() {
        return events;
    }

    @Override
    public void simulateOvertime() {
        // default no-op, override in sports where hasOvertime() = true
    }

    @Override
    public abstract void simulateNextSegment();

    @Override
    public abstract Object getCurrentScore();

    @Override
    public abstract Object getResult();
}
