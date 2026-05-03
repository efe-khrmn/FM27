package com.volleyball;

import com.interfaces.ISport;
import com.interfaces.ITactic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyballTactic implements ITactic, Serializable {
    private static final long serialVersionUID = 1L;
    private String tacticName;
    private Map<String, String> parameters;
    private static final List<String> AVAILABLE_TACTICS = new ArrayList<>();

    static {
        AVAILABLE_TACTICS.add("5-1");   // 5 hitters, 1 setter
        AVAILABLE_TACTICS.add("6-2");   // 6 hitters, 2 setters
        AVAILABLE_TACTICS.add("4-2");   // 4 hitters, 2 setters
    }

    public VolleyballTactic(String tacticName) {
        this.tacticName = tacticName;
        this.parameters = new HashMap<>();
        applyDefaultParameters(tacticName);
    }

    private void applyDefaultParameters(String tactic) {
        switch (tactic) {
            case "5-1":
                parameters.put("style", "balanced");
                parameters.put("serving", "aggressive");
                parameters.put("blocking", "medium");
                break;
            case "6-2":
                parameters.put("style", "attack");
                parameters.put("serving", "aggressive");
                parameters.put("blocking", "low");
                break;
            case "4-2":
                parameters.put("style", "defense");
                parameters.put("serving", "safe");
                parameters.put("blocking", "high");
                break;
            default:
                parameters.put("style", "balanced");
                parameters.put("serving", "safe");
                parameters.put("blocking", "medium");
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