package ru.maipomogator.bot.model;

import java.util.UUID;

public record Professor(Long id, String lastName, String firstName, String middleName, UUID siteId) {
    public String getFullName() {
        return lastName + " " + firstName + " " + middleName;
    }
}
