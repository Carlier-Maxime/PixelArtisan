package fr.metouais.pixelartisan.common.command.base;

public interface Command {
    void register();
    Command aliases(String... aliases);
    Command permissionLevel(int level);
    Command subcommand(Command subcommand);
    Command argument(CommandArgument<?> argument);
    Command execute(CommandExecutor executor);
}
