package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class AverageDurationFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Double> analyze(List<SleepingSession> sessions) {
        Double averageDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0.0);

        return new SleepAnalysisResult<>(AnalysisDescriptions.AVG_DURATION,
                Math.round(averageDuration * 100.0) / 100.0);
    }
}