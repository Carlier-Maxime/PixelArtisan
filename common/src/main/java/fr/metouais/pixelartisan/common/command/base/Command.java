package fr.metouais.pixelartisan.common.command.base;

public interface Command {
    void register();
    Command aliases(String... aliases);
    Command description(String description);
    Command permissionLevel(int level);
    Command subcommand(Command subcommand);
    Command execute(CommandExecutor executor);
}
