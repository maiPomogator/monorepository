package ru.maipomogator.config.gson;

import java.lang.reflect.Type;

/**
 * Интерфейс для адаптеров GSON, которые могут быть использованы для обработки объектов разных
 * типов.
 * <p>
 * На данный момент используется только для десериализации кривых данных от API МАИ.
 */
public interface TypeableAdapter {
    Type getTargetType();
}
