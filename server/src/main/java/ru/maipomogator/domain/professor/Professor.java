package ru.maipomogator.domain.professor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
import ru.maipomogator.domain.lesson.Lesson;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "professors", schema = "public")
public class Professor {

    /**
     * Идентификатор преподавателя
     */
    @ToString.Include
    @Id
    @SequenceGenerator(name = "professors_seq", sequenceName = "professors_id_seq", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "professors_seq")
    @Column
    private Long id;

    /**
     * Фамилия преподавателя
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Имя преподавателя
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Отчество преподавателя
     */
    @Column(name = "middle_name")
    private String middleName;

    /**
     * Оставшаяся часть ФИО
     */
    @Column(name = "other")
    private String other;

    /**
     * Идентификатор преподавателя, используемый на сайте МАИ
     */
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "site_id", unique = true)
    private UUID siteId;

    /**
     * Список занятий, которые ведет преподаватель
     */
    @ManyToMany(mappedBy = "professors", fetch = FetchType.LAZY)
    private Set<Lesson> lessons = new HashSet<>();

    public Professor(UUID siteId, String fio) {
        this(fio);
        this.siteId = siteId;
    }

    public Professor(String fio) {
        String[] parts = fio.split(" ");
        if (parts.length >= 1) {
            lastName = parts[0];
        }
        if (parts.length >= 2) {
            firstName = parts[1];
        }
        if (parts.length >= 3) {
            middleName = parts[2];
        }
        if (parts.length > 3) {
            // Если в строке ФИО больше трёх частей, объединяем остальные части в строку
            StringBuilder otherBuilder = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                otherBuilder.append(parts[i]);
                if (i < parts.length - 1) {
                    otherBuilder.append(" ");
                }
            }
            other = otherBuilder.toString();
        }
    }

    /**
     * Получить ФИО преподавателя
     *
     * @return ФИО
     */
    @ToString.Include(name = "fio", rank = 1)
    public String getFio() {
        return String.join(" ",
                lastName != null ? lastName : "",
                firstName != null ? firstName : "",
                middleName != null ? middleName : "",
                other != null ? other : "").trim();
    }

    public void addLesson(Lesson lsn) {
        lessons.add(lsn);
    }
}