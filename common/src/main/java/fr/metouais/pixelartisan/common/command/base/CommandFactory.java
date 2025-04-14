package fr.metouais.pixelartisan.common.command.base;

import java.util.function.Function;

public final class CommandFactory {
    private static Function<String, Command> construct = null;
    public static void setBuilder(Function<String, Command> construct) {
        CommandFactory.construct = construct;
    }
    public static Command builder(String commandName) {
        if (construct == null) throw new IllegalStateException("construct has not been set");
        return construct.apply(commandName);
    }
}
