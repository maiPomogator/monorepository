package ru.maipomogator.bot.processors;

import java.util.Collection;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

/**
 * Интерфейс, который определяет методы для обработки обновлений в Telegram-боте.
 *
 * @param <T> тип компонента обновления, который будет обрабатываться данным процессором
 */
public interface UpdateProcessor<T> {

    /**
     * Определяет, может ли данный процессор обработать конкретный компонент обновления.
     * 
     * @param updateEntity компонент обновления, который необходимо проверить
     * @return {@code true}, если процессор может обработать данное обновление, {@code false} в
     *         противном случае
     */
    boolean applies(T updateEntity);

    /**
     * Обрабатывает переданный компонент обновления и возвращает список запросов, которые необходимо
     * отправить к Telegram Bot API в ответ на это обновление.
     * 
     * @param updateEntity компонент обновления, который необходимо проверить
     * @return коллекция запросов к Telegram Bot API, которые необходимо выполнить после обработки
     *         данного обновления
     */
    Collection<? extends BaseRequest<?, ? extends BaseResponse>> process(T updateEntity);
}
