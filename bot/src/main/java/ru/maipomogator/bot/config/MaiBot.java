package ru.maipomogator.bot.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeAllPrivateChats;
import com.pengrad.telegrambot.request.GetMyCommands;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.GetMyCommandsResponse;

import lombok.extern.log4j.Log4j2;
import ru.maipomogator.bot.processors.message.command.CommandProcessor;

@Log4j2
@Component
public class MaiBot extends TelegramBot {

    public MaiBot(@Value("${bot.token}") String botToken, List<CommandProcessor> commands) {
        super(botToken);
        BotCommand[] botCommands = commands.stream().map(this::processorToCommand).toArray(BotCommand[]::new);
        this.execute(new SetMyCommands(botCommands).scope(new BotCommandScopeAllPrivateChats()));
        GetMyCommandsResponse resp = this.execute(new GetMyCommands().scope(new BotCommandScopeAllPrivateChats()));
        log.info("Bot initialized with {} commands.", resp.commands().length);
    }

    private BotCommand processorToCommand(CommandProcessor processor) {
        return new BotCommand(processor.getCommand(), processor.getDescription());
    }
}
