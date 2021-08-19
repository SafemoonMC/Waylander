package me.eduardwayland.mooncraft.waylander.command.builders;

import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class CommandBuilder<S extends CommandSender, T extends CommandBuilder<?, ?>> {

    /*
    Fields
     */
    private final String name;
    private String description;
    private Permission permission;

    private final List<CommandBuilder<?, ?>> children = new ArrayList<>();
    private final List<LiteralCommandBuilder<?, ?>> literals = new ArrayList<>();
    private final List<RequiredCommandBuilder<?, ?, ?>> arguments = new ArrayList<>();

    /*
    Constructor
     */
    protected CommandBuilder(@NotNull String name) {
        this.name = name;
    }

    /*
    Abstract Methods
     */
    protected abstract T getThis();

    public abstract Command build();

    /*
    Methods
     */
    @NotNull
    public T description(@NotNull String description) {
        this.description = description;
        return getThis();
    }

    @NotNull
    public T permission(@NotNull Permission permission) {
        this.permission = permission;
        return getThis();
    }

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