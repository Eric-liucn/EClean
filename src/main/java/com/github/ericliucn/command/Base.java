package com.github.ericliucn.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Base implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }

    public static CommandSpec build(){
        return CommandSpec.builder()
                .permission("eclean.base")
                .executor(new Base())
                .child(Clean.build(), "c", "clean")
                .child(Reload.build(), "reload")
                .build();
    }
}
