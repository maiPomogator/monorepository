package ru.maipomogator.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;

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
     * Наличие группы в файлах с сайта МАИ
     */
    @Column(name = "is_active")
    @JsonView(Views.IdInfo.class)
    private boolean isActive = true;

    /**
     * Список занятий группы
     */
    @ManyToMany(mappedBy = "groups")
    @JsonView(Views.FullView.class)
    private Set<Lesson> lessons = new HashSet<>();

    /**
     * Последний записанный хеш файла с расписанием
     */
    @Column(name = "latest_hash", length = 32)
    @JsonView(Views.FullView.class)
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
