package com.volleyball;

import com.interfaces.ICoach;

import java.io.Serializable;
import java.util.List;

class VolleyballCoach implements ICoach, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int experienceLevel;
    private boolean headCoach;
    private List<String> specializations;

    public VolleyballCoach(String name, int experienceLevel, boolean headCoach,
                           List<String> specializations) {
        this.name = name;
        this.experienceLevel = Math.max(1, Math.min(5, experienceLevel));
        this.headCoach = headCoach;
        this.specializations = specializations;
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getExperienceLevel() { return experienceLevel; }

    @Override
    public boolean isHeadCoach() { return headCoach; }

    @Override
    public List<String> getSpecializations() { return specializations; }
}