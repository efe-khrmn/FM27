package com.interfaces;

import java.util.List;

public interface ICoach {
    String getName();
    int getExperienceLevel();
    boolean isHeadCoach();
    List<String> getSpecializations();
}