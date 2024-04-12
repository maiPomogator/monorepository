package ru.maipomogator.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "siteId")
@ToString(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "professors", schema = "public")
public class Professor implements Comparable<Professor> {

    public static Professor copyOf(Professor original) {
        Professor copy = new Professor();
        copy.id = original.id;
        copy.lastName = original.lastName;
        copy.firstName = original.firstName;
        copy.middleName = original.middleName;
        copy.siteId = original.siteId;
        copy.lessons = new HashSet<>();
        return copy;
    }

    /**
     * Идентификатор группы
     */
    @ToString.Include
    @Id
    @SequenceGenerator(name = "professors_seq", sequenceName = "professors_id_seq", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "professors_seq")
    @Column
    @JsonView(Views.Id.class)
    private Long id;

    /**
     * Фамилия преподавателя
     */
    @Column(name = "last_name")
    @JsonView(Views.IdInfo.class)
    private String lastName = "";

    /**
     * Имя преподавателя
     */
    @Column(name = "first_name")
    @JsonView(Views.IdInfo.class)
    private String firstName = "";

    /**
     * Отчество преподавателя
     */
    @Column(name = "middle_name")
    @JsonView(Views.IdInfo.class)
    private String middleName = "";

    /**
     * Идентификатор преподавателя, используемый на mai.ru
     */
    @ToString.Include
    @Column(name = "site_id", unique = true)
    @JsonView(Views.IdInfo.class)
    private UUID siteId;

    /**
     * Список занятий, которые ведет преподаватель
     */
    @ManyToMany(mappedBy = "professors", fetch = FetchType.LAZY)
    @JsonView(Views.FullView.class)
    private Set<Lesson> lessons = new HashSet<>();

    /**
     * Получить ФИО преподавателя
     *
     * @return ФИО
     */
    @ToString.Include(name = "name", rank = 1)
    @JsonIgnore
    public String getFullName() {
        return lastName + " " + firstName + " " + middleName;
    }

    public void addLessons(Collection<Lesson> newLessons) {
        newLessons.forEach(this::addLesson);
    }

    public void addLesson(Lesson lsn) {
        lessons.add(lsn);
    }

    @Override
    public int compareTo(Professor other) {
        return getFullName().compareTo(other.getFullName());
    }
}