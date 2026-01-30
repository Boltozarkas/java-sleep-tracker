package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SleepingSession {
    private LocalDateTime sleepStart;
    private LocalDateTime sleepEnd;
    private SleepQuality quality;

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
        int sleepStartHour = sleepStart.getHour();
        int sleepEndHour = sleepEnd.getHour();

        // Если сон начался вечером/ночью и закончился утром
        if (sleepStartHour >= 18 || sleepStartHour < 6) {
            if (sleepEndHour >= 5 && sleepEndHour <= 11) {
                return true;
            }
        }
        return false;
    }

    public Chronotype getChronotype() {
        int sleepStartHour = sleepStart.getHour();
        int sleepEndHour = sleepEnd.getHour();

        if (sleepStartHour >= 23 && sleepEndHour >= 9) {
            return Chronotype.OWL;
        } else if (sleepStartHour <= 22 && sleepEndHour <= 7) {
            return Chronotype.LARK;
        } else {
            return Chronotype.DOVE;
        }
    }
}