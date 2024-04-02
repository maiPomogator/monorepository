package ru.maipomogator.parser.tasks;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import ru.maipomogator.parser.adapters.ParsedGroup;

/**
 * Класс для парсинга файла с расписанием группы. Реализует интерфейс Callable для возможности
 * асинхронного парсинга файлов.
 */
@RequiredArgsConstructor
public class ParseGroupTask implements Callable<ParsedGroup> {

    private final Path filePath;
    /**
     * Экземпляр для парсинга JSON-файлов
     */
    private final Gson gson;

    /**
     * Распарсивает файл с расписанием группы и возвращает имя группы и
     * 
     * @return группа с расписанием
     * @throws Exception при возникновении ошибки при парсинге файла
     */
    @Override
    public ParsedGroup call() throws Exception {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, new TypeToken<ParsedGroup>() {});
        } catch (JsonSyntaxException e) {
            throw new IllegalStateException(
                    "Error while parsing file %s".formatted(filePath.getFileName().toString()), e);
        }
    }
}
