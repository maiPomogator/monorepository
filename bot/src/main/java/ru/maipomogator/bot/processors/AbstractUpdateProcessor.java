package ru.maipomogator.bot.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import ru.maipomogator.bot.util.ButtonFactory;

public abstract class AbstractUpdateProcessor<T> implements UpdateProcessor<T> {
    // TODO сделать более продвинутую систему экранирования
    private static final String CHARS_TO_BE_ESCAPED = "[]()~`>#+-=|{}.!";

    private final Pattern pattern;
    private final Matcher matcher;
    // при необходимости обеспечения потокобезопасности заменить на ThreadLocal<Matcher>

    protected ButtonFactory buttons;

    protected AbstractUpdateProcessor(String regex) {
        this.pattern = Pattern.compile(regex);
        this.matcher = pattern.matcher("");
    }

    @Autowired
    public void setButtons(ButtonFactory buttons) {
        this.buttons = buttons;
    }

    protected boolean applies(String text) {
        matcher.reset(text);
        return matcher.matches();
    }

    protected String escapeForMarkdownV2(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            if (CHARS_TO_BE_ESCAPED.indexOf(c) != -1) {
                sb.append("\\");
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
