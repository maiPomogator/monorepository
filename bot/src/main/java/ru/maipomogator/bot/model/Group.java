package ru.maipomogator.bot.model;

public record Group(Long id, String name, int course, int faculty, String type) implements Comparable<Group> {

    @Override
    public int compareTo(Group other) {
        return this.name.compareTo(other.name);
    }
}
