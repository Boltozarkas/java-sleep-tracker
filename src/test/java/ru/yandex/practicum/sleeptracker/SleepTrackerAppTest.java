package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(AnalysisDescriptions.TOTAL_SESSIONS, result.getDescription());
    }

    @Test
    public void testMinDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 30),
                        LocalDateTime.of(2025, 10, 2, 15, 20),
                        SleepQuality.NORMAL
                )
        );

        MinDurationFunction function = new MinDurationFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        assertEquals(50L, result.getValue());
        assertEquals(AnalysisDescriptions.MIN_DURATION, result.getDescription());
    }

    @Test
    public void testMaxDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 30),
                        LocalDateTime.of(2025, 10, 2, 15, 20),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 21, 0),
                        LocalDateTime.of(2025, 10, 4, 9, 0),
                        SleepQuality.GOOD
                )
        );

        MaxDurationFunction function = new MaxDurationFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        assertEquals(720L, result.getValue());
        assertEquals(AnalysisDescriptions.MAX_DURATION, result.getDescription());
    }

    @Test
    public void testAverageDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 14, 30),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 0),
                        LocalDateTime.of(2025, 10, 4, 7, 0),
                        SleepQuality.GOOD
                )
        );

        AverageDurationFunction function = new AverageDurationFunction();
        SleepAnalysisResult<Double> result = function.analyze(sessions);

        assertEquals(330.0, result.getValue(), 0.01);
        assertEquals(AnalysisDescriptions.AVG_DURATION, result.getDescription());
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
        assertEquals(AnalysisDescriptions.BAD_QUALITY, result.getDescription());
    }

    @Test
    public void testChronotypeFunctionOwl() {
        // Сон с 23:30 до 9:30
        // Ложится в 23:30 (23 >= 23) -> сова
        // Встает в 9:30 (9 >= 9) -> сова
        // Результат: СОВА
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        assertEquals(Chronotype.OWL.getDisplayName(), result.getValue());
    }

    @Test
    public void testChronotypeFunctionOwlExactly() {
        // Граничный случай: сон с 23:00 до 9:00
        // Ложится в 23:00 (23 >= 23) -> сова
        // Встает в 9:00 (9 >= 9) -> сова
        // Результат: СОВА
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 9, 0),
                        SleepQuality.GOOD
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        assertEquals(Chronotype.OWL.getDisplayName(), result.getValue());
    }

    @Test
    public void testChronotypeFunctionLark() {
        // Сон с 21:30 до 6:30
        // Ложится в 21:30 (21 < 22) -> жаворонок
        // Встает в 6:30 (6 < 7) -> жаворонок
        // Результат: ЖАВОРОНОК
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 21, 30),
                        LocalDateTime.of(2025, 10, 2, 6, 30),
                        SleepQuality.GOOD
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        assertEquals(Chronotype.LARK.getDisplayName(), result.getValue());
    }

    @Test
    public void testChronotypeFunctionLarkExactly() {
        // Граничный случай: сон с 21:59 до 6:59
        // Ложится в 21:59 (21 < 22) -> жаворонок
        // Встает в 6:59 (6 < 7) -> жаворонок
        // Результат: ЖАВОРОНОК
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 21, 59),
                        LocalDateTime.of(2025, 10, 2, 6, 59),
                        SleepQuality.GOOD
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        assertEquals(Chronotype.LARK.getDisplayName(), result.getValue());
    }

    @Test
    public void testChronotypeFunctionDove() {
        // Сон с 22:30 до 7:30
        // Ложится в 22:30 (22 < 23) -> не сова
        // Встает в 7:30 (7:30 >= 7:00) -> не жаворонок (встает ПОСЛЕ или В 7:00)
        // Результат: ГОЛУБЬ
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 30),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepQuality.GOOD
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        assertEquals(Chronotype.DOVE.getDisplayName(), result.getValue());
        assertEquals(AnalysisDescriptions.CHRONOTYPE, result.getDescription());
    }

    @Test
    public void testChronotypeFunctionTieAllThree() {
        // Все три хронотипа встречаются по 1 разу - должен быть ГОЛУБЬ
        List<SleepingSession> sessions = Arrays.asList(
                // СОВА: 23:30-9:30
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                ),
                // ЖАВОРОНОК: 21:30-6:30
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 21, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 30),
                        SleepQuality.NORMAL
                ),
                // ГОЛУБЬ: 22:30-7:30 (не сова: 22 < 23, не жаворонок: 7:30 >= 7:00)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 22, 30),
                        LocalDateTime.of(2025, 10, 4, 7, 30),
                        SleepQuality.GOOD
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        // Все три хронотипа встречаются по 1 разу - должен быть ГОЛУБЬ
        assertEquals(Chronotype.DOVE.getDisplayName(), result.getValue());
    }

    @Test
    public void testChronotypeFunctionTieDoveAndOwl() {
        // Голубей и сов по 1 - должен быть ГОЛУБЬ
        List<SleepingSession> sessions = Arrays.asList(
                // Сова
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                ),
                // Голубь
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 22, 30),
                        LocalDateTime.of(2025, 10, 3, 7, 30),
                        SleepQuality.NORMAL
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        assertEquals(Chronotype.DOVE.getDisplayName(), result.getValue());
        assertEquals(AnalysisDescriptions.CHRONOTYPE, result.getDescription());
    }

    @Test
    public void testChronotypeFunctionInsufficientData() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 15, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 13, 30),
                        LocalDateTime.of(2025, 10, 2, 14, 15),
                        SleepQuality.NORMAL
                )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.analyze(sessions);

        assertEquals(AnalysisDescriptions.INSUFFICIENT_DATA, result.getValue());
        assertEquals(AnalysisDescriptions.CHRONOTYPE, result.getDescription());
    }

    @Test
    public void testSleeplessNightsFunctionBasic() {
        List<SleepingSession> sessions = Arrays.asList(
                // Сон с 23:00 до 7:00 - ночной сон (пересекает 0:00-6:00)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD
                ),
                // Сон с 22:00 до 5:00 - ночной сон
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 22, 0),
                        LocalDateTime.of(2025, 10, 3, 5, 0),
                        SleepQuality.NORMAL
                ),
                // Дневной сон 14:00-15:00 - НЕ ночной сон
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 4, 14, 0),
                        LocalDateTime.of(2025, 10, 4, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        // Период: с 1 окт (23:00) по 4 окт (15:00)
        // Первая запись после 12:00 -> первая ночь: 2 окт
        // Последняя запись после 12:00 -> последняя ночь: 4 окт
        // Ночи для анализа: 2 окт, 3 окт, 4 окт
        // Ночи со сном: 2 окт (первая сессия - ночь 1-2 окт)
        // Бессонные ночи: 3 окт, 4 окт
        assertEquals(2L, result.getValue());
    }

    @Test
    public void testSleeplessNightsFunctionEmptyFile() {
        List<SleepingSession> sessions = Collections.emptyList();

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        assertEquals(0L, result.getValue());
        assertEquals(AnalysisDescriptions.SLEEPLESS_NIGHTS, result.getDescription());
    }

    @Test
    public void testSleeplessNightsFunctionCrossMonth() {
        List<SleepingSession> sessions = Arrays.asList(
                // Ночь 30 сен - 1 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 9, 30, 23, 0),
                        LocalDateTime.of(2025, 10, 1, 7, 0),
                        SleepQuality.GOOD
                ),
                // Ночь 1-2 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.NORMAL
                ),
                // Дневной сон 2 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 15, 0),
                        SleepQuality.GOOD
                ),
                // Ночь 2-3 окт (начинается после полуночи)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 0, 30),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.GOOD
                )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        // Первая запись: 30 сен 23:00 (после 12:00) -> первая ночь: 1 окт
        // Последняя запись: 3 окт 8:00 (до 12:00) -> последняя ночь: 2 окт
        // Ночи для анализа: 1 окт, 2 окт
        // Ночи со сном: 1 окт (вторая сессия), 2 окт (четвертая сессия)
        assertEquals(0L, result.getValue());
    }

    @Test
    public void testSleeplessNightsFunctionFirstSessionAfterMidnight() {
        List<SleepingSession> sessions = Arrays.asList(
                // Сон 1 окт 0:10-6:20 (начался после полуночи)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 0, 10),
                        LocalDateTime.of(2025, 10, 1, 6, 20),
                        SleepQuality.GOOD
                ),
                // Ночь 1-2 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.NORMAL
                ),
                // Дневной сон 2 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 15, 0),
                        SleepQuality.GOOD
                )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        // Первая запись: 1 окт 0:10 (до 12:00) -> первая ночь: 1 окт
        // Последняя запись: 2 окт 15:00 (после 12:00) -> последняя ночь: 2 окт
        // Ночи для анализа: 1 окт, 2 окт
        // Ночи со сном: 1 окт (первая сессия - ночь 30 сен-1 окт)
        // Бессонные ночи: 2 окт
        assertEquals(1L, result.getValue());
    }

    @Test
    public void testSleeplessNightsFunctionMultipleSleeplessNights() {
        List<SleepingSession> sessions = Arrays.asList(
                // Ночь 1-2 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD
                ),
                // Дневной сон 2 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 15, 0),
                        SleepQuality.NORMAL
                ),
                // Дневной сон 3 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 13, 0),
                        LocalDateTime.of(2025, 10, 3, 14, 0),
                        SleepQuality.GOOD
                ),
                // Ночь 4-5 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 4, 22, 30),
                        LocalDateTime.of(2025, 10, 5, 6, 0),
                        SleepQuality.NORMAL
                )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        // Первая запись: 1 окт 23:00 (после 12:00) -> первая ночь: 2 окт
        // Последняя запись: 5 окт 6:00 (до 12:00) -> последняя ночь: 4 окт
        // Ночи для анализа: 2 окт, 3 окт, 4 окт
        // Ночи со сном: 4 окт (четвертая сессия - ночь 4-5 окт)
        // Бессонные ночи: 2 окт, 3 окт
        assertEquals(2L, result.getValue());
    }

    @Test
    public void testSleeplessNightsFunctionAllNightsHaveSleep() {
        List<SleepingSession> sessions = Arrays.asList(
                // Ночь 1-2 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                // Ночь 2-3 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 30),
                        LocalDateTime.of(2025, 10, 3, 7, 0),
                        SleepQuality.NORMAL
                ),
                // Ночь 3-4 окт
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 21, 45),
                        LocalDateTime.of(2025, 10, 4, 5, 30),
                        SleepQuality.GOOD
                )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        // Первая запись: 1 окт 22:00 (после 12:00) -> первая ночь: 2 окт
        // Последняя запись: 4 окт 5:30 (до 12:00) -> последняя ночь: 3 окт
        // Ночи для анализа: 2 окт, 3 окт
        // Ночи со сном: 2 окт (вторая сессия - ночь 2-3 окт), 3 окт (третья сессия - ночь 3-4 окт)
        assertEquals(0L, result.getValue());
    }

    @Test
    public void testSleeplessNightsFunctionSimpleCase() {
        // Простой тест для понимания логики
        List<SleepingSession> sessions = Arrays.asList(
                // Ночь 1-2 окт: сон есть
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD
                ),
                // Ночь 3-4 окт: сон есть
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 22, 0),
                        LocalDateTime.of(2025, 10, 4, 6, 0),
                        SleepQuality.NORMAL
                )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.analyze(sessions);

        // Первая запись: 1 окт 23:00 (после 12:00) -> первая ночь: 2 окт
        // Последняя запись: 4 окт 6:00 (до 12:00) -> последняя ночь: 3 окт (минус 1 день)
        // Ночи для анализа: 2 окт, 3 окт
        // Ночи со сном: 3 окт (вторая сессия)
        // Бессонные ночи: 2 окт
        assertEquals(1L, result.getValue());
    }
}