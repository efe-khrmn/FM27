package com.abstracts;

import com.interfaces.*;
import java.util.List;

public abstract class AbstractSport implements ISport, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Override
    public abstract String getSportName();

    @Override
    public abstract int getTeamSize();

    @Override
    public abstract int getMaxSubstitutions();

    @Override
    public abstract int getSegmentCount();

    @Override
    public abstract String getSegmentLabel(int index);

    @Override
    public abstract boolean hasOvertime();

    @Override
    public abstract List<String> getPositions();

    @Override
    public abstract List<String> getOverallTypes();

    @Override
    public abstract IStandingsRules getStandingsRules();

    @Override
    public abstract IPlayer createPlayer(String name, String position);

    @Override
    public abstract ITeam createTeam(String name, String logoId);

    @Override
    public abstract IMatch createMatch(ITeam home, ITeam away);

    @Override
    public abstract ITrainingSession createTraining(ITeam team);
}