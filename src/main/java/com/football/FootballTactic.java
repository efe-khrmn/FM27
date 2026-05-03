package com.football;

import com.interfaces.ISport;
import com.interfaces.ITactic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootballTactic implements ITactic, Serializable {
    private static final long serialVersionUID = 1L;
    private String tacticName;
    private Map<String, String> parameters;

    private static final List<String> AVAILABLE_TACTICS = new ArrayList<>();

    static {
        AVAILABLE_TACTICS.add("4-4-2");
        AVAILABLE_TACTICS.add("4-3-3");
        AVAILABLE_TACTICS.add("3-5-2");
        AVAILABLE_TACTICS.add("5-3-2");
    }

    public FootballTactic(String tacticName) {
        this.tacticName = tacticName;
        this.parameters = new HashMap<>();
        applyDefaultParameters(tacticName);
    }

    private void applyDefaultParameters(String tactic) {
        switch (tactic) {
            case "4-4-2":
                parameters.put("pressing", "medium");
                parameters.put("playMaking", "crossing");
                parameters.put("defensiveLine", "medium");
                parameters.put("style", "balanced");
                break;
            case "4-3-3":
                parameters.put("pressing", "high");
                parameters.put("playMaking", "tiki taka");
                parameters.put("defensiveLine", "high");
                parameters.put("style", "attack");
                break;
            case "3-5-2":
                parameters.put("pressing", "medium");
                parameters.put("playMaking", "possession");
                parameters.put("defensiveLine", "medium");
                parameters.put("style", "midfield");
                break;
            case "5-3-2":
                parameters.put("pressing", "low");
                parameters.put("playMaking", "counter attack");
                parameters.put("defensiveLine", "low");
                parameters.put("style", "defense");
                break;
            default:
                parameters.put("pressing", "medium");
                parameters.put("playMaking", "mixed");
                parameters.put("defensiveLine", "medium");
                parameters.put("style", "balanced");
        }
    }

    @Override
    public String getTacticName() { return tacticName; }

    @Override
    public Map<String, String> getParameters() { return parameters; }

    @Override
    public boolean isValid(ISport sport) {
        return AVAILABLE_TACTICS.contains(tacticName);
    }

    @Override
    public List<String> getAvailableNames() { return AVAILABLE_TACTICS; }
}