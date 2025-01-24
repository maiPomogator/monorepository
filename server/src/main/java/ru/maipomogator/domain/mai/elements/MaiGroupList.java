package ru.maipomogator.domain.mai.elements;

import java.util.ArrayList;
import java.util.Collection;

import ru.maipomogator.domain.group.Group;

/**
 * Специальный тип данных для ответа API МАИ, чтобы не смешивать собственный десериализатор с
 * другими типами в обычных запросах
 * 
 * @see ru.maipomogator.config.gson.adapters.mai.MaiGroupListDeserializer
 */
public class MaiGroupList extends ArrayList<Group> {
    public MaiGroupList(Collection<? extends Group> c) {
        super(c);
    }
}
