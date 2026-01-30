package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SleepTrackerAppTest {

    @Test
    public void testTotalSessionsFunction() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 15),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.NORMAL
                )
        );

        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult<Integer> result = function.analyze(sessions);

        assertEquals(2, result.getValue());
        assertEquals("Общее количество сессий сна", result.getDescription());
    }

    @Test
    public void testMinDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0), // 8 часов = 480 минут
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 30),
                        LocalDateTime.of(2025, 10, 2, 15, 20), // 50 минут
                        SleepQuality.NORMAL
                )
        );

        MinDurationFunction function = new MinDurationFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        assertEquals(50L, result.getValue());
    }

    @Test
    public void testBadQualitySessionsFunction() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 15),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.BAD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 30),
                        LocalDateTime.of(2025, 10, 3, 15, 20),
                        SleepQuality.BAD
                )
        );

        BadQualitySessionsFunction function = new BadQualitySessionsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        assertEquals(2L, result.getValue());
    }

    @Test
    public void testChronotypeFunction() {
        // Тест для совы (ложится после 23, встает после 9)
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 45),
                        LocalDateTime.of(2025, 10, 3, 10, 0),
                        SleepQuality.NORMAL
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        assertEquals("Сова", result.getValue());
    }

    @Test
    public void testSleeplessNightsFunction() {
        List<SleepingSession> sessions = Arrays.asList(
                // Ночь с 1 на 2 октября - есть сон
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD
                ),
                // Ночь с 2 на 3 октября - есть сон
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 22, 0),
                        LocalDateTime.of(2025, 10, 3, 5, 0),
                        SleepQuality.NORMAL
                ),
                // Ночь с 3 на 4 октября - НЕТ сна (только дневной)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 4, 14, 0),
                        LocalDateTime.of(2025, 10, 4, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        // Ожидаем 1 бессонную ночь (3-4 октября)
        assertTrue(result.getValue() > 0);
    }
}