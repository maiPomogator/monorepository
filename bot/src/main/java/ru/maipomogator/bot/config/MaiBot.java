package ru.maipomogator.bot.config;

import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MaiBot extends TelegramBot {
    public MaiBot(BotConfig botConfig) {
        super(botConfig.getToken());
    }
}
