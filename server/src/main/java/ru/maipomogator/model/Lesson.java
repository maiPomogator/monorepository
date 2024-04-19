package ru.maipomogator.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "name", "types", "date", "timeStart", "rooms", "professors" })
@Entity
@Table(name = "lessons", schema = "public")
public class Lesson implements Comparable<Lesson> {

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
    @ElementCollection
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
    @ElementCollection
    @Column(name = "room")
    @JsonView(Views.IdInfo.class)
    private Set<String> rooms = new HashSet<>();

    /**
     * Наличие занятия в файлах с сайта МАИ
     */
    @Column(name = "is_active")
    @JsonProperty(value = "isActive")
    @JsonView(Views.IdInfo.class)
    private boolean isActive = true;

    /**
     * Группы занятия
     */
    @ManyToMany
    @JoinTable(name = "lessons_groups", joinColumns = @JoinColumn(name = "lesson_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    @JsonIgnoreProperties({ "lessons" })
    @JsonView(Views.FullView.class)
    private Set<Group> groups = new HashSet<>();

    /**
     * Преподаватели занятия
     */
    @ManyToMany
    @JoinTable(name = "lessons_professors", joinColumns = @JoinColumn(name = "lesson_id"), inverseJoinColumns = @JoinColumn(name = "professor_id"))
    @JsonIgnoreProperties({ "lessons" })
    @JsonView(Views.FullView.class)
    private Set<Professor> professors = new HashSet<>();

    public void addGroup(Group gr) {
        groups.add(gr);
    }

    public void addProfessor(Professor pr) {
        professors.add(pr);
    }

    public void addRoom(String room) {
        rooms.add(room);
    }

    public void removeRoom(String room) {
        rooms.remove(room);
    }

    public void addType(LessonType type) {
        types.add(type);
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
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

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", name=" + name +
                ", types=" + types.toString() +
                ", date=" + date +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", groups=[" + streamToString(groups.stream().map(Group::getName).sorted()) + "]" +
                ", professors=[" + streamToString(professors.stream().map(Professor::getFullName).sorted()) + "]" +
                ", rooms=" + rooms.toString() +
                '}';
    }

    private String streamToString(Stream<String> stream) {
        return stream.collect(Collectors.joining(","));
    }

}