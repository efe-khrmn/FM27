package com.engine;

import com.football.FootballSport;
import com.interfaces.ISport;
import com.volleyball.VolleyballSport;

public class SportFactory {
    private static final long serialVersionUID = 1L;

    public static ISport create(String sportName) {
        if (sportName == null) {
            throw new IllegalArgumentException("Sport name cannot be null");
        }

        return switch (sportName.toLowerCase()) {
            case "football" -> new FootballSport();
            case "Volleyball" -> new VolleyballSport();
            default -> throw new IllegalArgumentException("Unknown sport: " + sportName);
        };
    }
}