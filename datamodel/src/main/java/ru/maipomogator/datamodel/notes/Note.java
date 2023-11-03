package ru.maipomogator.datamodel.notes;

import java.awt.Color;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.maipomogator.datamodel.timetable.Lesson;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "notes", schema = "public")
public class Note {

    /**
     * Идентификатор группы
     */
    @Id
    @SequenceGenerator(name = "notes_seq", sequenceName = "notes_id_seq", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notes_seq")
    @Column
    private Long id;

    /**
     * Дата и время, к которым относится заметка
     */
    @Column
    private LocalDateTime targetTimestamp;

    /**
     * Заголовок заметки
     */
    @Column
    private String title;

    /**
     * Текст заметки
     */
    @Column
    private String text;

    /**
     * Цвет метки данной заметки
     */
    @Column
    private Color color;

    /**
     * Флаг, указывающий на завершенность заметки
     */
    @Column(name = "is_completed")
    private boolean isCompleted;

    /**
     * Занятие, к которому относится заметка
     */
    @ManyToOne
    private Lesson lesson;
}
