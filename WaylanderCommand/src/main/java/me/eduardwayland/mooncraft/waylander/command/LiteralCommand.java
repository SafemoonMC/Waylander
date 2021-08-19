package me.eduardwayland.mooncraft.waylander.command;

import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.command.executor.LiteralExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class LiteralCommand<S extends CommandSender> extends Command {

    /*
    Fields
     */
    private final @NotNull String[] aliases;
    private final @Nullable LiteralExecutor<S> executor;

    /*
    Constructor
     */
    public LiteralCommand(@NotNull String name, @NotNull String description, @Nullable Permission permission, @NotNull String[] aliases, @Nullable LiteralExecutor<S> literalExecutor) {
        super(name, description, permission);
        this.aliases = aliases;
        this.executor = literalExecutor;
    }
}