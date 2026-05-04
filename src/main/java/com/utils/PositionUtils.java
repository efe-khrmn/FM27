package com.utils;

public class PositionUtils {

    public static String getPositionGroup(String pos) {
        if (pos == null) return "unknown";

        switch (pos.toUpperCase()) {
            case "ST": case "LW": case "RW": case "CAM": return "attack";
            case "CM": case "CDM": return "midfield";
            case "CB": case "LB": case "RB": return "defense";
            case "GK": return "goalkeeper";
            default: return "unknown";
        }
    }
}