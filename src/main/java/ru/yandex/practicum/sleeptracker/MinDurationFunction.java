package ru.yandex.practicum.sleeptracker;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MinDurationFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        Optional<Long> minDuration = sessions.stream()
                .map(SleepingSession::getDurationInMinutes)
                .min(Comparator.naturalOrder());

        return new SleepAnalysisResult<>(AnalysisDescriptions.MIN_DURATION,
                minDuration.orElse(0L));
    }
}