package me.eduardwayland.mooncraft.waylander.command;

import com.mojang.brigadier.arguments.ArgumentType;
import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.command.executor.RequiredExecutor;
import me.eduardwayland.mooncraft.waylander.command.suggest.Suggestions;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class RequiredCommand<S extends CommandSender, T> extends Command {

    /*
    Fields
     */
    private final @NotNull ArgumentType<T> argumentType;
    private final @Nullable Suggestions suggestions;
    private final @Nullable RequiredExecutor<S> executor;

    /*
    Constructor
     */
    public RequiredCommand(@NotNull String name, @NotNull String description, @Nullable Permission permission, @NotNull ArgumentType<T> argumentType, @Nullable Suggestions suggestions, @Nullable RequiredExecutor<S> requiredExecutor) {
        super(name, description, permission);
        this.argumentType = argumentType;
        this.suggestions = suggestions;
        this.executor = requiredExecutor;
    }
}