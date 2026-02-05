package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    //Привет!=)
    public static void main(String[] args) {
        // Проверяем, есть ли аргумент с путем к файлу
        if (args.length < 1) {
            System.out.println("Пожалуйста, укажите путь к файлу с логом сна");
            return;
        }

        // Получаем путь к файлу из аргументов
        String filename = args[0];

        // Читаем данные из файла
        List<SleepingSession> sleepData = readSleepData(filename);

        // Если данных нет - завершаем программу
        if (sleepData.isEmpty()) {
            System.out.println("Нет данных для анализа");
            return;
        }

        System.out.println("=== АНАЛИЗ СНА ===\n");

        // Создаем все функции анализа
        SleepAnalysisFunction[] functions = {
                new TotalSessionsFunction(),
                new MinDurationFunction(),
                new MaxDurationFunction(),
                new AverageDurationFunction(),
                new BadQualitySessionsFunction(),
                new SleeplessNightsFunction(),
                new ChronotypeFunction()
        };

        // Преобразуем массив в список
        List<SleepAnalysisFunction> functionList = Arrays.asList(functions);

        // Выполняем все функции и выводим результаты
        functionList.stream()
                .map(func -> func.analyze(sleepData))
                .forEach(System.out::println);
    }

    // Метод для чтения данных о сне из файла
    private static List<SleepingSession> readSleepData(String filename) {
        try (var lines = Files.lines(Paths.get(filename))) {
            // Преобразуем каждую строку в объект SleepingSession
            return lines
                    .map(SleepTrackerApp::parseSleepSession) // Используем метод для парсинга
                    .filter(session -> session != null)     // Убираем null
                    .collect(Collectors.toList());          // Собираем в список
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Метод для парсинга одной строки с данными о сне
    private static SleepingSession parseSleepSession(String line) {
        try {
            // Разделяем строку по точкам с запятой
            String[] parts = line.split(";");

            // Проверяем, что строка содержит все 3 части
            if (parts.length != 3) {
                System.out.println("Некорректная строка: " + line);
                return null;
            }

            // Создаем форматтер для даты
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

            // Преобразуем строки в даты
            LocalDateTime startTime = LocalDateTime.parse(parts[0].trim(), dateFormat);
            LocalDateTime endTime = LocalDateTime.parse(parts[1].trim(), dateFormat);

            // Получаем качество сна
            SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

            // Создаем и возвращаем объект SleepingSession
            return new SleepingSession(startTime, endTime, quality);

        } catch (Exception e) {
            System.out.println("Ошибка парсинга строки: " + line + " - " + e.getMessage());
            return null;
        }
    }
}