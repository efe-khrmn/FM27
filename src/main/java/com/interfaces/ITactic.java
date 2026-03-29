package com.interfaces;

import java.util.List;
import java.util.Map;

public interface ITactic {
    String getTacticName();
    Map<String, String> getParameters();
    boolean isValid(ISport sport);
    List<String> getAvailableNames();
}
