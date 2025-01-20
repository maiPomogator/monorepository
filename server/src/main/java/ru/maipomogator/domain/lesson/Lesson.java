package ru.maipomogator.domain.lesson;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import ru.maipomogator.domain.group.Group;
import ru.maipomogator.domain.professor.Professor;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

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
    private Long id;

    /**
     * Название занятия
     */
    @EqualsAndHashCode.Include
    @Column
    private String name;

    /**
     * Типы занятия (в исходных данных может быть указано несколько)
     *
     * @see LessonType
     */
    @EqualsAndHashCode.Include
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type")
    private Set<LessonType> types = new HashSet<>();

    /**
     * День занятия
     */
    @EqualsAndHashCode.Include
    @Column
    private LocalDate date;

    /**
     * Время начала занятия
     */
    @EqualsAndHashCode.Include
    @Column(name = "time_start")
    private LocalTime timeStart;

    /**
     * Время окончания занятия
     */
    @Column(name = "time_end")
    private LocalTime timeEnd;

    /**
     * Аудитории занятия
     */
    @EqualsAndHashCode.Include
    @ElementCollection
    @Column(name = "room")
    private Set<String> rooms = new HashSet<>();

    /**
     * Наличие занятия в файлах с сайта МАИ
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Группы занятия
     */
    @ManyToMany
    @JoinTable(name = "lessons_groups", joinColumns = @JoinColumn(name = "lesson_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();

    /**
     * Преподаватели занятия
     */
    @EqualsAndHashCode.Include
    @ManyToMany
    @JoinTable(name = "lessons_professors", joinColumns = @JoinColumn(name = "lesson_id"), inverseJoinColumns = @JoinColumn(name = "professor_id"))
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

    public Boolean isActive() {
        return isActive;
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
                ", professors=[" + streamToString(professors.stream().map(Professor::getFio).sorted()) + "]" +
                ", rooms=" + rooms.toString() +
                '}';
    }

    private String streamToString(Stream<String> stream) {
        return stream.collect(Collectors.joining(","));
    }
}