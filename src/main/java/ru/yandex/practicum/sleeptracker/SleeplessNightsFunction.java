package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SleeplessNightsFunction implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult<Long> analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>(AnalysisDescriptions.SLEEPLESS_NIGHTS, 0L);
        }

        // 1. Находим все ночи, когда был сон
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(SleepingSession::isNightSleep) // Фильтруем только ночные сессии
                .map(this::getNightDateForSession)     // Получаем дату ночи для каждой сессии
                .collect(Collectors.toSet());

        // 2. Находим весь период логирования
        LocalDateTime firstDateTime = sessions.get(0).getSleepStart();
        LocalDateTime lastDateTime = sessions.get(sessions.size() - 1).getSleepEnd();

        // 3. Определяем первую и последнюю ночь для анализа
        LocalDate firstNight = getFirstNightDate(firstDateTime);
        LocalDate lastNight = getLastNightDate(lastDateTime);

        // 4. Если первая ночь позже последней, возвращаем 0
        if (firstNight.isAfter(lastNight)) {
            return new SleepAnalysisResult<>(AnalysisDescriptions.SLEEPLESS_NIGHTS, 0L);
        }

        // 5. Создаем поток всех ночей в периоде
        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;

        // 6. Считаем бессонные ночи
        long sleeplessNights = IntStream.range(0, (int) totalNights)
                .mapToObj(i -> firstNight.plusDays(i)) // Генерируем все даты ночей
                .filter(night -> !nightsWithSleep.contains(night)) // Оставляем только бессонные
                .count(); // Считаем количество

        return new SleepAnalysisResult<>(AnalysisDescriptions.SLEEPLESS_NIGHTS, sleeplessNights);
    }

    private LocalDate getNightDateForSession(SleepingSession session) {
        // Ночью считается дата, когда наступает полночь (0:00)
        // Если сон пересекает полночь, то это ночь той даты, когда наступает 0:00

        LocalDateTime sleepStart = session.getSleepStart();
        LocalDateTime sleepEnd = session.getSleepEnd();

        // Проверяем, пересекает ли сон полночь
        // Если время начала и окончания сна в разные дни
        if (!sleepStart.toLocalDate().equals(sleepEnd.toLocalDate())) {
            // Сон пересекает полночь - ночь относится к дате, когда начался сон
            return sleepStart.toLocalDate();
        }

        // Сон в пределах одного дня
        // Если сон начался вечером (после 18:00) - это ночь этой даты
        if (sleepStart.getHour() >= 18) {
            return sleepStart.toLocalDate();
        } else {
            // Если сон начался утром или днем - это ночь предыдущей даты
            return sleepStart.toLocalDate().minusDays(1);
        }
    }

    private LocalDate getFirstNightDate(LocalDateTime firstDateTime) {
        // Определяем первую ночь для анализа
        LocalDate firstDate = firstDateTime.toLocalDate();

        if (firstDateTime.getHour() >= SleepingSession.NOON_HOUR) {
            // Если первая запись после 12:00, первая ночь - следующая
            return firstDate.plusDays(1);
        } else {
            // Если первая запись до 12:00, первая ночь - сегодняшняя
            return firstDate;
        }
    }

    private LocalDate getLastNightDate(LocalDateTime lastDateTime) {
        // Последняя ночь - это ночь даты последней записи
        // Но если последняя запись утром (до 12:00), это ночь предыдущей даты
        LocalDate lastDate = lastDateTime.toLocalDate();

        if (lastDateTime.getHour() < SleepingSession.NOON_HOUR) {
            return lastDate.minusDays(1);
        } else {
            return lastDate;
        }
    }
}