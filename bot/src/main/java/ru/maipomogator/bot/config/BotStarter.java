package ru.maipomogator.bot.config;

import java.util.List;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeAllPrivateChats;
import com.pengrad.telegrambot.request.GetMyCommands;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.message.command.CommandProcessor;

@Log4j2
@Component
@RequiredArgsConstructor
public class BotStarter {

    private final ApplicationEventPublisher eventPublisher;

    private final UpdatesListener listener;
    private final TelegramBot bot;
    private final List<CommandProcessor> commandProcessors;

    @EventListener(classes = ApplicationStartedEvent.class)
    private void initializeBot() {
        setCommands();
        // todo получение информации о боте для логирования

        eventPublisher.publishEvent(bot);
    }

    private void setCommands() {
        BotCommand[] commands = commandProcessors.stream()
                .map(CommandProcessor::getBotCommand)
                .toArray(BotCommand[]::new);

        BaseResponse response = bot.execute(new SetMyCommands(commands).scope(new BotCommandScopeAllPrivateChats()));
        log.info(response);
        BaseResponse commandsResponse = bot.execute(new GetMyCommands().scope(new BotCommandScopeAllPrivateChats()));
        log.info(commandsResponse);

    }

    @EventListener(classes = TelegramBot.class)
    private void startProcessingUpdates() {
        bot.setUpdatesListener(listener);
        log.info("Update listener was set.");
    }
}
