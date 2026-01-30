package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<String> analyze(List<SleepingSession> sessions) {
        // Фильтруем только ночные сессии
        List<SleepingSession> nightSessions = sessions.stream()
                .filter(SleepingSession::isNightSleep)
                .collect(Collectors.toList());

        if (nightSessions.isEmpty()) {
            return new SleepAnalysisResult<>("Хронотип пользователя", "Недостаточно данных");
        }

        // Подсчитываем количество каждого хронотипа
        Map<Chronotype, Long> chronotypeCounts = nightSessions.stream()
                .collect(Collectors.groupingBy(
                        SleepingSession::getChronotype,
                        Collectors.counting()
                ));

        // Находим максимальное количество
        long maxCount = chronotypeCounts.values().stream()
                .max(Long::compare)
                .orElse(0L);

        // Находим все хронотипы с максимальным количеством
        List<Chronotype> maxChronotypes = chronotypeCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Определяем итоговый хронотип
        Chronotype resultChronotype;
        if (maxChronotypes.size() == 1) {
            resultChronotype = maxChronotypes.get(0);
        } else {
            resultChronotype = Chronotype.DOVE;
        }

        return new SleepAnalysisResult<>("Хронотип пользователя", resultChronotype.getDisplayName());
    }
}