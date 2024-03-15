package ru.maipomogator.bot.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractUpdateProcessor<T> implements UpdateProcessor<T> {
    private final Pattern pattern;
    private final Matcher matcher;
    // при необходимости обеспечения потокобезопасности заменить на ThreadLocal<Matcher>

    protected AbstractUpdateProcessor(String regex) {
        this.pattern = Pattern.compile(regex);
        this.matcher = pattern.matcher("");
    }

    public boolean applies(String text) {
        matcher.reset(text);
        return matcher.matches();
    }
}
