package ru.maipomogator.domain.group;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ru.maipomogator.domain.lesson.Lesson;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "name")
@ToString(of = { "id", "name" })

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
     * Наличие группы в файлах с сайта МАИ
     */
    @Column(name = "is_active")
    @JsonProperty(value = "isActive")
    private Boolean isActive = true;

    /**
     * Название группы
     */
    @Column(unique = true)
    private String name;

    /**
     * Номер курса группы
     */
    @Column
    private Integer course;

    /**
     * Номер факультета группы
     */
    @Column
    private Integer faculty;

    /**
     * Тип группы
     * 
     * @see GroupType
     */
    @Column
    @Enumerated(EnumType.STRING)
    private GroupType type;

    /**
     * Список занятий группы
     */
    @ManyToMany(mappedBy = "groups")
    private Set<Lesson> lessons = new HashSet<>();

    /**
     * Последний записанный хеш файла с расписанием
     */
    @Column(name = "latest_hash", length = 32)
    @JsonIgnore
    private String latestHash;

    public void addLesson(Lesson lsn) {
        lessons.add(lsn);
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }
}
