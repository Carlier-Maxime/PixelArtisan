package fr.metouais.pixelartisan.common.command.base;

public interface CommandExecutor {
    default void exec(CommandContext ctx) {
        try {
            __exec(ctx);
        } catch (CommandException e) {
            ctx.getSender().send("§c"+e.getMessage());
        }
    }
    void __exec(CommandContext ctx);
}
