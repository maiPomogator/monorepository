package ru.maipomogator.bot.config;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;

@Component
public class BotStarter {

    private final UpdatesListener listener;
    private final TelegramBot bot;

    public BotStarter(UpdatesListener listener, TelegramBot bot) {
        this.listener = listener;
        this.bot = bot;
    }

    @EventListener(classes = ApplicationStartedEvent.class)
    private void start() {
        this.bot.setUpdatesListener(listener);
    }
}
