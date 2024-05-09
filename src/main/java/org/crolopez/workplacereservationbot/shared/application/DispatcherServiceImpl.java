package org.crolopez.workplacereservationbot.shared.application;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.crolopez.workplacereservationbot.shared.application.entities.command.CommandEntity;
import org.crolopez.workplacereservationbot.shared.application.entities.command.EventEntity;

import jakarta.inject.Inject;

import java.util.Optional;

@Singleton
@Slf4j
public class DispatcherServiceImpl implements DispatcherService {

    @Inject
    CommandContainerImpl commandContainer;

    @Override
    public String process(EventEntity eventEntity) {
        log.info(String.format("Dispatching %s command", eventEntity.getCommand()));

        Optional<CommandEntity> command = commandContainer.getCommands()
                .stream().filter(x -> x.getAlias().equals(eventEntity.getCommand())).findAny();
        return command.isPresent()
                ? command.get().process(eventEntity.getArgs())
                : "Command not found";
    }
}
