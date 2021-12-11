package me.eduardwayland.mooncraft.waylander.command.builders;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.command.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class CommandBuilder<S extends CommandSender, T extends CommandBuilder<?, ?>> {

    /*
    Fields
     */
    private final @NotNull String name;
    private final @NotNull List<CommandBuilder<?, ?>> children = new ArrayList<>();
    private final @NotNull List<LiteralCommandBuilder<?, ?>> literals = new ArrayList<>();
    private final @NotNull List<RequiredCommandBuilder<?, ?, ?>> arguments = new ArrayList<>();
    private @Nullable String description;
    private @Nullable Permission permission;

    /*
    Constructor
     */
    protected CommandBuilder(@NotNull String name) {
        this.name = name;
    }

    /*
    Abstract Methods
     */

    /**
     * @return the current builder instance
     */
    protected abstract T getThis();

    /**
     * @return a Command instance obtained from this command
     */
    public abstract Command build();

    /*
    Methods
     */

    /**
     * Sets a new description
     *
     * @param description a description, used in help command too
     * @return the updated builder instance
     */
    @NotNull
    public T description(@NotNull String description) {
        this.description = description;
        return getThis();
    }

    /**
     * Sets a new permission requirement
     *
     * @param permission a permission instance
     * @return the updated builder instance
     */
    @NotNull
    public T permission(@NotNull Permission permission) {
        this.permission = permission;
        return getThis();
    }

    /**
     * Appends a new command tree to the current one
     *
     * @param commandBuilder a new command builder instance representing the new command tree
     * @return the updated builder instance
     */
    @NotNull
    public T then(@NotNull CommandBuilder<?, ?> commandBuilder) {
        children.add(commandBuilder);
        if (commandBuilder instanceof LiteralCommandBuilder)
            literals.add((LiteralCommandBuilder<?, ?>) commandBuilder);
        else if (commandBuilder instanceof RequiredCommandBuilder)
            arguments.add((RequiredCommandBuilder<?, ?, ?>) commandBuilder);
        return getThis();
    }
}