package ru.maipomogator.model;

import java.awt.Color;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "notes", schema = "public")
public class Note implements Comparable<Note> {

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
    @ManyToOne(optional = true)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Override
    public int compareTo(Note o) {
        return getTime().compareTo(o.getTime());
    }

    private LocalDateTime getTime() {
        if (lesson != null) {
            return lesson.getTimeStart().atDate(lesson.getDate());
        } else if (targetTimestamp != null) {
            return targetTimestamp;
        } else {
            throw new IllegalStateException("Nor lesson nor targetTimestamp are set in Note %d".formatted(id));
        }
    }
}
