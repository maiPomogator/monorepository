package ru.maipomogator.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "name", "types", "date", "timeStart", "rooms" })

@Entity
@Table(name = "lessons", schema = "public")
public class Lesson implements Comparable<Lesson> {

    public static Lesson copyOf(Lesson original) {
        Lesson copy = new Lesson();
        copy.id = original.id;
        copy.name = original.name;
        copy.types = new HashSet<>(original.types);
        copy.date = original.date;
        copy.timeStart = original.timeStart;
        copy.timeEnd = original.timeEnd;
        copy.groups = new HashSet<>();
        copy.professors = new HashSet<>();
        copy.rooms = new HashSet<>(original.rooms);
        copy.status = original.status;
        return copy;
    }

    /**
     * Идентификатор группы
     */
    @Id
    @SequenceGenerator(name = "lessons_seq", sequenceName = "lessons_id_seq", allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lessons_seq")
    @Column
    @JsonView(Views.Id.class)
    private Long id;

    /**
     * Название занятия
     */
    @Column
    @JsonView(Views.IdInfo.class)
    private String name;

    /**
     * Типы занятия (в исходных данных может быть указано несколько)
     *
     * @see LessonType
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type")
    @JsonView(Views.IdInfo.class)
    private Set<LessonType> types = new HashSet<>();

    /**
     * День занятия
     */
    @Column
    @JsonView(Views.IdInfo.class)
    private LocalDate date;

    /**
     * Время начала занятия
     */
    @Column(name = "time_start")
    @JsonView(Views.IdInfo.class)
    private LocalTime timeStart;

    /**
     * Время окончания занятия
     */
    @Column(name = "time_end")
    @JsonView(Views.IdInfo.class)
    private LocalTime timeEnd;

    /**
     * Аудитории занятия
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "room")
    @JsonView(Views.IdInfo.class)
    private Set<String> rooms = new HashSet<>();

    /**
     * Статус занятия
     * 
     * @see LessonStatus
     */
    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(Views.IdInfo.class)
    private LessonStatus status = LessonStatus.CREATED;

    /**
     * Группы занятия
     */
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "lessons_groups", joinColumns = @JoinColumn(name = "lesson_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    @JsonIgnoreProperties({ "lessons" })
    @JsonView(Views.FullView.class)
    private Set<Group> groups = new HashSet<>();

    /**
     * Преподаватели занятия
     */
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "lessons_professors", joinColumns = @JoinColumn(name = "lesson_id"), inverseJoinColumns = @JoinColumn(name = "professor_id"))
    @JsonIgnoreProperties({ "lessons" })
    @JsonView(Views.FullView.class)
    private Set<Professor> professors = new HashSet<>();

    @PrePersist
    private void prePersistLesson() {
        this.status = LessonStatus.SAVED;
    }

    public void setGroups(Set<Group> newGroups) {
        this.groups = newGroups;
        newGroups.forEach(g -> g.addLesson(this));
    }

    public void setProfessors(Set<Professor> newProfessors) {
        this.professors = newProfessors;
        newProfessors.forEach(p -> p.addLesson(this));
    }

    public void addGroups(Collection<Group> newGroups) {
        newGroups.forEach(this::addGroup);
    }

    public void addGroup(Group gr) {
        if (!groups.contains(gr)) {
            groups.add(gr);
            gr.addLesson(this);
        }
    }

    public void removeGroup(Group gr) {
        groups.remove(gr);
    }

    public void addProfessors(Collection<Professor> newProfessors) {
        newProfessors.forEach(this::addProfessor);
    }

    public void addProfessor(Professor pr) {
        if (!professors.contains(pr)) {
            professors.add(pr);
            pr.addLesson(this);
        }
    }

    public void removeProfessor(Professor pr) {
        professors.remove(pr);
    }

    public void addRooms(Collection<String> newRooms) {
        newRooms.forEach(this::addRoom);
    }

    public void addRoom(String room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
        }
    }

    public void removeRoom(String room) {
        rooms.remove(room);
    }

    public void addTypes(Collection<LessonType> newTypes) {
        newTypes.forEach(this::addType);
    }

    public void addType(LessonType type) {
        if (!types.contains(type)) {
            types.add(type);
        }
    }

    /**
     * Сравнение занятий производится по дате и времени начала
     * 
     * @param other занятие для сравнения
     */
    @Override
    public int compareTo(Lesson other) {
        return Comparator.comparing(Lesson::getDate).thenComparing(Lesson::getTimeStart).compare(this, other);
    }

    @SuppressWarnings("null")
    @JsonIgnore
    public long getHash() {
        Hasher hasher = Hashing.murmur3_128().newHasher();
        hasher.putUnencodedChars(name);
        hasher.putUnencodedChars(streamToString(types.stream().sorted().map(LessonType::name)));
        hasher.putUnencodedChars(date.toString());
        hasher.putUnencodedChars(timeStart.toString());
        hasher.putUnencodedChars(streamToString(rooms.stream().sorted(String::compareTo)));
        return hasher.hash().asLong();
    }

    private String streamToString(Stream<String> stream) {
        return stream.collect(Collectors.joining(","));
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", types=" + types.toString() +
                ", date=" + date +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", groups=[" + streamToString(groups.stream().sorted().map(Group::getName)) + "]" +
                ", professors=[" + streamToString(professors.stream().sorted().map(Professor::getFullName)) + "]" +
                ", rooms=" + rooms.toString() +
                '}';
    }
}
