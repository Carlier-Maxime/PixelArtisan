package fr.metouais.pixelartisan.common.command.base;

public interface Command extends CommandNode {
    void register();
    Command aliases(String... aliases);
    Command permissionLevel(int level);
    Command subcommand(Command subcommand);
    @Override
    Command execute(CommandExecutor executor);
    @Override
    Command argument(CommandArgument<?> argument);
}
