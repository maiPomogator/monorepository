package ru.maipomogator.updaters.mai;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Future.State;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import ru.maipomogator.model.Group;
import ru.maipomogator.model.Lesson;
import ru.maipomogator.model.Professor;
import ru.maipomogator.service.GroupService;
import ru.maipomogator.service.LessonService;
import ru.maipomogator.service.ProfessorService;
import ru.maipomogator.updaters.tasks.DownloadFileTask;

// @RequiredArgsConstructor
@Log4j2
@Component("newUpdater")
public class MaiUpdater {
    private static final String BASE_URL = "https://public.mai.ru/schedule/data/";
    private static final String GROUPS_FILE_URL = BASE_URL + "groups.json";
    private final Path baseFolder;
    private Path groupsJsonPath;
    private Path groupsFolder;

    private final GroupService groupService;
    private final ProfessorService professorService;
    private final LessonService lessonService;

    private final Gson gson;
    private Map<Lesson, Lesson> mapLessons;

    @SneakyThrows
    public MaiUpdater(GroupService groupService, ProfessorService professorService, LessonService lessonService,
            Gson gson) {
        this.groupService = groupService;
        this.professorService = professorService;
        this.lessonService = lessonService;
        this.gson = gson;
        baseFolder = Files.createTempDirectory("mai-updater");
    }

    @Scheduled(cron = "0 0 4 * * *")
    @SneakyThrows({ IOException.class, InterruptedException.class })
    @Transactional
    public void update() {
        Instant start = Instant.now();
        Path currentRunFolder = baseFolder.resolve(LocalDate.now().toString());
        groupsJsonPath = currentRunFolder.resolve("groups.json");
        groupsFolder = currentRunFolder.resolve("groups");
        mapLessons = new HashMap<>(400_000);
        Files.createDirectories(currentRunFolder);
        Collection<Group> existingGroups = groupService.findAll();
        log.info("Got {} groups from db", existingGroups.size());
        Collection<Group> groupFromFile = getGroupsFromFile();
        log.info("Got {} groups from file.", groupFromFile.size());

        // TODO придумать, что делать с такими группами
        Collection<Group> missingGroups = new ArrayList<>(existingGroups);
        missingGroups.removeAll(groupFromFile);

        Collection<Group> newGroups = new ArrayList<>(groupFromFile);
        newGroups.removeAll(existingGroups);

        Collection<Group> commonGroups = new ArrayList<>(existingGroups);
        commonGroups.retainAll(groupFromFile);

        log.info("commonGroups: {}, missingGroups: {}, newGroups: {}",
                commonGroups.size(), missingGroups.size(), newGroups.size());

        Collection<Group> savedGroups = groupService.saveAll(newGroups);
        commonGroups.addAll(savedGroups);

        try (ExecutorService es = Executors.newFixedThreadPool(5)) {
            Files.createDirectories(groupsFolder);
            Collection<Callable<File>> downloads = new ArrayList<>(savedGroups.size());
            for (Group group : commonGroups) {
                Files.exists(groupsJsonPath);
                String fileName = DigestUtils.md5DigestAsHex(group.getName().getBytes(StandardCharsets.UTF_8))
                        + ".json";
                Path groupFilePath = groupsFolder.resolve(fileName);
                downloads.add(new DownloadFileTask(BASE_URL + fileName, groupFilePath));
            }
            Collection<Future<File>> futureFiles = es.invokeAll(downloads, 45, TimeUnit.SECONDS);
            futureFiles.stream().filter(f -> f.state().equals(State.FAILED)).forEach(Future::exceptionNow);

        }

        for (Group group : commonGroups) {
            Collection<Lesson> existingLessons = lessonService.findAllLessonsForGroup(group);
            log.info("Got {} lessons from db for group {}", existingLessons.size(), group.getName());

            Collection<Lesson> lessonsFromFile = parseLessons(group.getName());
            log.info("Got {} lessons from file for group {}", lessonsFromFile.size(), group.getName());

            Collection<Lesson> commonLessons = new ArrayList<>(existingLessons);
            commonLessons.retainAll(lessonsFromFile);
            Collection<Lesson> lessonsToActivate = commonLessons.stream()
                    .filter(((Predicate<Lesson>) Lesson::isActive).negate()).toList();
            if (!lessonsToActivate.isEmpty()) {
                lessonsToActivate.forEach(Lesson::activate);
                lessonService.saveAll(lessonsToActivate);
            }

            Collection<Lesson> missingLessons = new ArrayList<>(existingLessons);
            missingLessons.removeAll(lessonsFromFile);
            missingLessons.forEach(Lesson::deactivate);
            if (!missingLessons.isEmpty()) {
                lessonService.saveAll(missingLessons);
            }

            Collection<Lesson> newLessons = new ArrayList<>(lessonsFromFile);
            newLessons.removeAll(existingLessons);
            newLessons = newLessons.stream().map(this::processLesson).toList();
            newLessons.forEach(l -> l.addGroup(group));
            if (!newLessons.isEmpty()) {
                lessonService.saveAll(newLessons);
            }

            log.info("commonLessons: {}, missingLessons: {}, newLessons: {}",
                    commonLessons.size(), missingLessons.size(), newLessons.size());
            log.info("Ended processing {} group", group.getName());
        }

        Instant end = Instant.now();

        log.error("Total number of combined lessons: {}", mapLessons.size());
        log.info("Time spent: {} ms.", Duration.between(start, end).toMillis());

        System.out.println("THE END");
    }

    private Lesson processLesson(Lesson rawLesson) {
        return mapLessons.computeIfAbsent(rawLesson, lesson -> {
            Set<Professor> newProfessors = lesson.getProfessors().stream().map(professorService::findOrSave)
                    .collect(Collectors.toSet());
            lesson.setProfessors(newProfessors);
            return lesson;
        });
    }

    @SneakyThrows(IOException.class)
    private Collection<Lesson> parseLessons(String groupName) {
        String fileName = DigestUtils.md5DigestAsHex(groupName.getBytes(StandardCharsets.UTF_8)) + ".json";
        Path groupFilePath = groupsFolder.resolve(fileName);
        try (BufferedReader reader = Files.newBufferedReader(groupFilePath, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, new TypeToken<>() {});
        } catch (JsonSyntaxException e) {
            throw new IllegalStateException(
                    "Error while parsing file %s".formatted(groupFilePath.getFileName().toString()), e);
        }
    }

    private Collection<Group> getGroupsFromFile() {
        File groupsFile = getGroupsFile();
        try (Reader groupsReader = Files.newBufferedReader(groupsFile.toPath())) {
            return gson.fromJson(groupsReader, new TypeToken<List<Group>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("IO error while reading groups.json");
        }
    }

    @SneakyThrows(IOException.class)
    private File getGroupsFile() {
        File maybeFile = groupsJsonPath.toFile();
        if (maybeFile.isFile()) {
            return maybeFile;
        }

        log.debug("Actual groups.json wasn`t found. Downloading new.");
        DownloadFileTask task = new DownloadFileTask(GROUPS_FILE_URL, groupsJsonPath);
        return task.call();
    }
}
