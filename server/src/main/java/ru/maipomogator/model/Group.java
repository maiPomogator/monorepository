package ru.maipomogator.model;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "groups", schema = "public")
public class Group implements Comparable<Group> {

    /**
     * Идентификатор группы
     */
    @Id
    @SequenceGenerator(name = "groups_seq", sequenceName = "groups_id_seq", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groups_seq")
    @Column
    @JsonView(Views.Id.class)
    private Long id;

    /**
     * Название группы
     */
    @Column(unique = true)
    @JsonView(Views.IdInfo.class)
    private String name;

    /**
     * Номер курса группы
     */
    @Column
    @JsonView(Views.IdInfo.class)
    private Integer course;

    /**
     * Номер факультета группы
     */

    @Column
    @JsonView(Views.IdInfo.class)
    private Integer faculty;

    /**
     * Тип группы
     * 
     * @see GroupType
     */
    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(Views.IdInfo.class)
    private GroupType type;

    /**
     * Список занятий группы
     */
    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    @JsonView(Views.FullView.class)
    private Set<Lesson> lessons = new HashSet<>();

    public void setLessons(SortedSet<Lesson> newLessons) {
        this.lessons = newLessons;
        newLessons.forEach(l -> l.addGroup(this));
    }

    public void addLessons(Collection<Lesson> newLessons) {
        newLessons.forEach(this::addLesson);
    }

    public void addLesson(Lesson lsn) {
        if (!lessons.contains(lsn)) {
            lessons.add(lsn);
            lsn.addGroup(this);
        }
    }

    @JsonIgnore
    public String getMd5OfName() {
        return DigestUtils.md5DigestAsHex(name.getBytes(StandardCharsets.UTF_8));
    }

    // TODO реализовать hashCode
    @Override
    public int compareTo(Group other) {
        return this.name.compareTo(other.name);
    }
}
