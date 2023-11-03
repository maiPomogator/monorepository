package ru.maipomogator.datamodel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import ru.maipomogator.datamodel.timetable.Group;
import ru.maipomogator.datamodel.timetable.Lesson;
import ru.maipomogator.datamodel.timetable.Professor;
import ru.maipomogator.datamodel.timetable.enums.GroupType;
import ru.maipomogator.datamodel.timetable.enums.LessonType;

public class Test {
    public static void main(String[] args) {
        LessonType lType = LessonType.CONSULTATION;
        System.out.println(lType);
        GroupType gType = GroupType.BACHELOR;
        System.out.println(gType);
        Group gr = new Group();
        Professor pr = new Professor();
        Lesson lsn = new Lesson();
        lsn.setName("Test");
        lsn.setDay(LocalDate.now());
        lsn.setTimeStart(LocalTime.now());
        lsn.setTimeEnd(LocalTime.now());
        lsn.setTypes(List.of(LessonType.LECTURE));
        lsn.setRooms(List.of("404В"));
        lsn.addGroup(gr);
        lsn.addProfessor(pr);
        System.out.println(lsn);
    }
}
