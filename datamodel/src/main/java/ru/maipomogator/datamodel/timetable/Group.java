package ru.maipomogator.datamodel.timetable;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.util.DigestUtils;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.maipomogator.datamodel.timetable.enums.GroupType;

@Data
@NoArgsConstructor
@RequiredArgsConstructor

@Entity
@Table(name = "groups", schema = "public")
public class Group {

    /**
     * Идентификатор группы
     */
    @Id
    @SequenceGenerator(name = "groups_seq", sequenceName = "groups_id_seq", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groups_seq")
    @Column
    private Long id;

    /**
     * Название группы
     */
    @NonNull
    @Column(unique = true)
    private String name;

    /**
     * Номер курса группы
     */
    @NonNull
    @Column
    private Integer course;

    /**
     * Номер факультета группы
     */

    @NonNull
    @Column
    private Integer faculty;

    /**
     * Тип группы
     * 
     * @see GroupType
     */
    @NonNull
    @Column
    @Enumerated(EnumType.STRING)
    private GroupType type;

    /**
     * Список занятий группы
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private SortedSet<Lesson> lessons = new TreeSet<>();

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

    public String getMd5OfName() {
        return DigestUtils.md5DigestAsHex(name.getBytes(StandardCharsets.UTF_8));
    }
}
