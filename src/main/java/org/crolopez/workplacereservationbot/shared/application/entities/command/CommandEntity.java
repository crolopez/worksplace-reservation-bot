package org.crolopez.workplacereservationbot.shared.application.entities.command;

import lombok.Getter;

@Getter
public abstract class CommandEntity {
    protected String alias;

    protected abstract String launch(String... args);
    protected abstract int getExpectedArgs();
    public String process(String... args) {
        if (args.length < getExpectedArgs()) {
            return String.format("Invalid number of arguments. Expected: %d", getExpectedArgs());
        }

        return launch(args);
    }
}
