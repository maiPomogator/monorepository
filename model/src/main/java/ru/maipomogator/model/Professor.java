package ru.maipomogator.model;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@RequiredArgsConstructor

@Entity
@Table(name = "professors", schema = "public")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Professor implements Comparable<Professor> {

    public static Professor copyOf(Professor original) {
        Professor copy = new Professor();
        copy.id = original.id;
        copy.lastName = original.lastName;
        copy.firstName = original.firstName;
        copy.middleName = original.middleName;
        copy.siteId = original.siteId;
        copy.lessons = new TreeSet<>();
        return copy;
    }

    /**
     * Идентификатор группы
     */
    @Id
    @SequenceGenerator(name = "professors_seq", sequenceName = "professors_id_seq", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "professors_seq")
    @Column
    private Long id;

    /**
     * Фамилия преподавателя
     */
    @Column(name = "last_name")
    private String lastName = "";

    /**
     * Имя преподавателя
     */
    @Column(name = "first_name")
    private String firstName = "";

    /**
     * Отчество преподавателя
     */
    @Column(name = "middle_name")
    private String middleName = "";

    /**
     * Идентификатор преподавателя, используемый на mai.ru
     */
    @NonNull
    @Column(name = "site_id", unique = true)
    private UUID siteId;

    /**
     * Список занятий, которые ведет преподаватель
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "professors", fetch = FetchType.LAZY)
    @JsonBackReference
    private SortedSet<Lesson> lessons = new TreeSet<>();

    /**
     * Получить ФИО преподавателя
     *
     * @return ФИО
     */
    @JsonIgnore
    public String getFullName() {
        return lastName + " " + firstName + " " + middleName;
    }

    public void setLessons(SortedSet<Lesson> newLessons) {
        this.lessons = newLessons;
        newLessons.forEach(l -> l.addProfessor(this));
    }

    public void addLessons(Collection<Lesson> newLessons) {
        newLessons.forEach(this::addLesson);
    }

    public void addLesson(Lesson lsn) {
        if (!lessons.contains(lsn)) {
            lessons.add(lsn);
            lsn.addProfessor(this);
        }
    }

    @Override
    public int compareTo(Professor other) {
        return getFullName().compareTo(other.getFullName());
    }
}