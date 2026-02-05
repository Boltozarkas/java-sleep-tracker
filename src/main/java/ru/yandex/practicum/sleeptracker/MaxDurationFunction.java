package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MaxDurationFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        Long maxDuration = sessions.stream()
                .map(SleepingSession::getDurationInMinutes)
                .max(Long::compare)
                .orElse(0L);

        return new SleepAnalysisResult<>(AnalysisDescriptions.MAX_DURATION, maxDuration);
    }
}