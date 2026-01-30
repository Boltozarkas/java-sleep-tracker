package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SleeplessNightsFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        // Находим даты всех ночных сессий сна
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(session -> {
                    LocalDateTime start = session.getSleepStart();
                    LocalDateTime end = session.getSleepEnd();

                    // Проверяем, пересекается ли сессия с ночным временем (0:00-6:00)
                    // Если сон начинается до 6 утра или заканчивается после полуночи
                    if (start.getHour() < 6 || end.getHour() >= 0) {
                        // Проверяем, что сон действительно ночной
                        // (пересекается с интервалом 0:00-6:00)
                        LocalDate sleepDate = start.toLocalDate();
                        LocalDateTime nightStart = sleepDate.atStartOfDay();
                        LocalDateTime nightEnd = sleepDate.atTime(6, 0);

                        // Проверяем пересечение интервалов
                        boolean startsBeforeNightEnd = start.isBefore(nightEnd);
                        boolean endsAfterNightStart = end.isAfter(nightStart);

                        return startsBeforeNightEnd && endsAfterNightStart;
                    }
                    return false;
                })
                .map(session -> {
                    // Для сна, начавшегося вечером и закончившегося утром,
                    // считаем ночью дату, когда начался сон
                    LocalDateTime start = session.getSleepStart();
                    if (start.getHour() >= 18) {
                        return start.toLocalDate();
                    } else {
                        // Если сон начался ночью (после 0:00), это предыдущая дата
                        return start.minusDays(1).toLocalDate();
                    }
                })
                .collect(Collectors.toSet());

        // Находим общее количество ночей в периоде логирования
        LocalDateTime firstSleep = sessions.get(0).getSleepStart();
        LocalDateTime lastSleep = sessions.get(sessions.size() - 1).getSleepEnd();

        // Определяем начальную и конечную даты для подсчета ночей
        LocalDate startDate = firstSleep.toLocalDate();
        if (firstSleep.getHour() >= 12) {
            startDate = startDate.plusDays(1); // Следующая ночь
        }

        LocalDate endDate = lastSleep.toLocalDate();

        // Генерируем все даты в интервале
        Set<LocalDate> allNights = IntStream.iterate(0, i -> i + 1)
                .limit(Period.between(startDate, endDate.plusDays(1)).getDays())
                .mapToObj(startDate::plusDays)
                .collect(Collectors.toSet());

        // Бессонные ночи = все ночи - ночи со сном
        long sleeplessNights = allNights.stream()
                .filter(night -> !nightsWithSleep.contains(night))
                .count();

        return new SleepAnalysisResult<>("Количество бессонных ночей", sleeplessNights);
    }
}