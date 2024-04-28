package ru.maipomogator.bot.model;

public record Group(Long id, String name) implements Comparable<Group> {

    @Override
    public int compareTo(Group other) {
        return this.name.compareTo(other.name);
    }
}
