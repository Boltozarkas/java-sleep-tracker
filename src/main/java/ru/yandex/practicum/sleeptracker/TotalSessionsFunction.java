package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class TotalSessionsFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Integer> analyze(List<SleepingSession> sessions) {
        int count = sessions.size();
        return new SleepAnalysisResult<>(AnalysisDescriptions.TOTAL_SESSIONS, count);
    }
}