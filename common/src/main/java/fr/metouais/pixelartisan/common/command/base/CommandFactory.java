package fr.metouais.pixelartisan.common.command.base;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CommandFactory {
    private final Function<String, Command> construct;
    private final CommandArgumentFactory argsFactory;
    private CommandFactory(Function<String, Command> construct, CommandArgumentFactory argsFactory) {
        this.construct = construct;
        this.argsFactory = argsFactory;
    }
    public Command internalBuilder(String commandName) {
        return construct.apply(commandName);
    }
    public CommandArgumentFactory getArgsFactory() {
        return argsFactory;
    }
    private static CommandFactory instance = new CommandFactory(null, null){
        private static final IllegalStateException EXCEPTION = new IllegalStateException("CommandFactory has not been set");

        @Override
        public Command internalBuilder(String commandName) {
            throw EXCEPTION;
        }

        @Override
        public CommandArgumentFactory getArgsFactory() {
            throw EXCEPTION;
        }
    };
    synchronized
    public static void setFactory(@NotNull Function<String, Command> construct, @NotNull CommandArgumentFactory argsFactory) {
        instance = new CommandFactory(construct, argsFactory);
    }
    public static Command builder(String commandName) {
        return instance.internalBuilder(commandName);
    }
    public static CommandArgumentFactory argsFactory() {
        return instance.getArgsFactory();
    }
}
