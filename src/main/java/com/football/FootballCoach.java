package com.football;

import com.interfaces.ICoach;
import java.util.ArrayList;
import java.util.List;

public class FootballCoach implements ICoach {
    private String name;
    private int experienceLevel;
    private boolean isHeadCoach;
    private List<String> specializations;

    public FootballCoach(String name,int experienceLevel,boolean isHeadCoach,List<String> specializations){
        if (name == null) {
            throw new IllegalArgumentException("Coach name must not be blank.");
        }
        this.name=name;
    this.experienceLevel=Math.max(1,Math.min(5,experienceLevel));
    this.isHeadCoach=isHeadCoach;
    this.specializations=specializations != null ? new ArrayList<String>(specializations) : new ArrayList<String>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getExperienceLevel() {
        return experienceLevel;
    }

    @Override
    public boolean isHeadCoach() {
        return isHeadCoach;
    }

    // Buradaki return kısmını sonrasında düzelt

    @Override
    public List<String> getSpecializations() {
        return specializations;
    }
    public boolean hasSpecialization(String spec) {
        return specializations.contains(spec);
    }
    @Override
    public String toString() {
        String role = isHeadCoach ? "Head Coach" : "Assistant Coach";
        return String.format("%s [%s | Level %d | %s]",
                name, role, experienceLevel, specializations);
    }
}
