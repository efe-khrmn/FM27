package com.interfaces;
import java.util.List;

public interface ITrainingSession {
    List<String> getTrainingTypes();
    void runTraining(String type);
    List<Object> getResults();
}