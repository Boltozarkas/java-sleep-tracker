package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SleepingSession {
    private LocalDateTime sleepStart;
    private LocalDateTime sleepEnd;
    private SleepQuality quality;

    // Константы времени
    public static final int MIDNIGHT_HOUR = 0;           // 0:00
    public static final int SIX_AM_HOUR = 6;             // 6:00
    public static final int SEVEN_AM_HOUR = 7;           // 7:00
    public static final int NINE_AM_HOUR = 9;            // 9:00
    public static final int NOON_HOUR = 12;              // 12:00
    public static final int TEN_PM_HOUR = 22;            // 22:00
    public static final int ELEVEN_PM_HOUR = 23;         // 23:00

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, SleepQuality quality) {
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.quality = quality;
    }

    public long getDurationInMinutes() {
        return ChronoUnit.MINUTES.between(sleepStart, sleepEnd);
    }

    public LocalDateTime getSleepStart() {
        return sleepStart;
    }

    public LocalDateTime getSleepEnd() {
        return sleepEnd;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public boolean isNightSleep() {
        // Сон считается ночным, если он пересекает интервал от 0:00 до 6:00
        LocalDate startDate = sleepStart.toLocalDate();

        // Проверяем текущую ночь (0:00-6:00 текущей даты)
        LocalDateTime nightStart = startDate.atStartOfDay();
        LocalDateTime nightEnd = startDate.atTime(SIX_AM_HOUR, 0);

        boolean intersectsCurrentNight = sleepStart.isBefore(nightEnd) &&
                sleepEnd.isAfter(nightStart);

        // Проверяем следующую ночь (0:00-6:00 следующей даты)
        LocalDate nextDate = startDate.plusDays(1);
        LocalDateTime nextNightStart = nextDate.atStartOfDay();
        LocalDateTime nextNightEnd = nextDate.atTime(SIX_AM_HOUR, 0);

        boolean intersectsNextNight = sleepStart.isBefore(nextNightEnd) &&
                sleepEnd.isAfter(nextNightStart);

        return intersectsCurrentNight || intersectsNextNight;
    }

    public Chronotype getChronotype() {
        // Сначала проверяем, что это ночной сон
        if (!isNightSleep()) {
            return Chronotype.DOVE; // Для не ночного сна возвращаем голубя
        }

        int sleepStartHour = sleepStart.getHour();
        int sleepEndHour = sleepEnd.getHour();

        // СОВА: засыпание ПОСЛЕ 23:00 И пробуждение ПОСЛЕ 9:00
        // Интерпретируем "после 23:00" как час >= 23 и минуты > 0, или просто час >= 23
        // Для простоты будем считать, что если час = 23, это уже "после 23:00"
        boolean isOwl = sleepStartHour >= ELEVEN_PM_HOUR && sleepEndHour >= NINE_AM_HOUR;

        // ЖАВОРОНОК: засыпание ДО 22:00 И пробуждение ДО 7:00
        // Интерпретируем "до 22:00" как час < 22
        boolean isLark = sleepStartHour < TEN_PM_HOUR && sleepEndHour < SEVEN_AM_HOUR;

        // Определяем хронотип
        if (isOwl) {
            return Chronotype.OWL;
        } else if (isLark) {
            return Chronotype.LARK;
        } else {
            return Chronotype.DOVE;
        }
    }
}