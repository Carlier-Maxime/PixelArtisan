package fr.metouais.pixelartisan.common.command.base;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CommandFactory {
    private final Function<String, Command> construct;
    private CommandFactory(Function<String, Command> construct) {
        this.construct = construct;
    }
    public Command internalBuilder(String commandName) {
        return construct.apply(commandName);
    }
    private static CommandFactory instance = new CommandFactory(null){
        @Override
        public Command internalBuilder(String commandName) {
            throw new IllegalStateException("construct has not been set");
        }
    };
    synchronized
    public static void setBuilder(@NotNull Function<String, Command> construct) {
        instance = new CommandFactory(construct);
    }
    public static Command builder(String commandName) {
        return instance.internalBuilder(commandName);
    }
}
