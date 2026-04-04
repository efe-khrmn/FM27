package com.engine;

import com.football.FootballSport;
import com.interfaces.ISport;

public class SportFactory {

    public static ISport create(String sportName) {
        if (sportName == null) {
            throw new IllegalArgumentException("Sport name cannot be null");
        }

        return switch (sportName.toLowerCase()) {
            case "football" -> new FootballSport();
            default -> throw new IllegalArgumentException("Unknown sport: " + sportName);
        };
    }
}