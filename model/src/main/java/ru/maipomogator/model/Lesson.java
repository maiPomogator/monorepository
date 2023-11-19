package ru.maipomogator.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data

@Entity
@Table(name = "lessons", schema = "public")
public class Lesson implements Comparable<Lesson> {

    public static Lesson copyOf(Lesson original) {
        Lesson copy = new Lesson();
        copy.id = original.id;
        copy.name = original.name;
        copy.types = new ArrayList<>(original.types);
        copy.day = original.day;
        copy.timeStart = original.timeStart;
        copy.timeEnd = original.timeEnd;
        copy.groups = new HashSet<>();
        copy.professors = new HashSet<>();
        copy.rooms = new ArrayList<>(original.rooms);
        copy.cancelled = original.cancelled;
        return copy;
    }

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
    @Column
    private String name;

    /**
     * Типы занятия(в исходных данных может быть указано несколько)
     *
     * @see LessonType
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type")
    private List<LessonType> types = new ArrayList<>();

    /**
     * День занятия
     */
    @Column
    private LocalDate day;

    /**
     * Время начала занятия
     */
    @Column(name = "time_start")
    private LocalTime timeStart;

    /**
     * Время окончания занятия
     */
    @Column(name = "time_end")
    private LocalTime timeEnd;

    /**
     * Группы занятия
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "lessons_professors", joinColumns = @JoinColumn(name = "lesson_id"), inverseJoinColumns = @JoinColumn(name = "professor_id"))
    private Set<Group> groups = new HashSet<>();

    /**
     * Преподаватели занятия
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "lessons_groups", joinColumns = @JoinColumn(name = "lesson_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Professor> professors = new HashSet<>();

    /**
     * Аудитории занятия
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "room")
    private List<String> rooms = new ArrayList<>();

    /**
     * Флаг, указывающий на удаление занятия после обновления расписания (возможно, будет заменен на
     * ENUM состояний в дальнейшем)
     */
    @Column(name = "is_cancelled")
    private boolean cancelled = false;

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
     * Сравнение занятий производится по дню и номеру
     * 
     * @param otherLesson занятие для сравнения
     */
    @Override
    public int compareTo(@NonNull Lesson otherLesson) {
        return Comparator.comparing(Lesson::getDay).thenComparing(Lesson::getTimeStart).compare(this, otherLesson);
    }

    public long getHash() {
        Hasher hasher = Hashing.murmur3_128().newHasher();
        hasher.putUnencodedChars(name);
        hasher.putUnencodedChars(types.toString());
        hasher.putUnencodedChars(day.toString());
        hasher.putUnencodedChars(timeStart.toString());
        hasher.putUnencodedChars(rooms.toString());
        return hasher.hash().asLong();
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "name='" + name + '\'' +
                ", types=" + types.toString() +
                ", day=" + day +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", number_of_groups=" + groups.size() +
                ", number_of_professors=" + professors.size() +
                ", rooms=" + rooms.toString() +
                '}';
    }
}
