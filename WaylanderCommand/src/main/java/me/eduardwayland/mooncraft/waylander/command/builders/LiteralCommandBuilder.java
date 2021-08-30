package me.eduardwayland.mooncraft.waylander.command.builders;

import me.eduardwayland.mooncraft.waylander.command.LiteralCommand;
import me.eduardwayland.mooncraft.waylander.command.executor.LiteralExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiteralCommandBuilder<S extends CommandSender, T> extends CommandBuilder<S, LiteralCommandBuilder<S, ?>> {

    /*
    Fields
     */
    private @Nullable String[] aliases;
    private @Nullable LiteralExecutor<S> executor;

    /*
    Constructor
     */
    protected LiteralCommandBuilder(@NotNull String name) {
        super(name);
    }

    /*
    Static Methods
     */
    @NotNull
    public static <S extends CommandSender> LiteralCommandBuilder<S, ?> name(@NotNull String name) {
        return new LiteralCommandBuilder<>(name);
    }

    /*
    Methods
     */
    @NotNull
    public LiteralCommandBuilder<S, T> aliases(@NotNull String[] args) {
        this.aliases = args;
        return getThis();
    }

    @NotNull
    public LiteralCommandBuilder<S, T> executes(@NotNull LiteralExecutor<S> executor) {
        this.executor = executor;
        return getThis();
    }

    /*
    Override Methods
     */
    @Override
    protected LiteralCommandBuilder<S, T> getThis() {
        return this;
    }

    @Override
    public LiteralCommand<S> build() {
        LiteralCommand<S> command = new LiteralCommand<S>(getName(), getDescription() == null ? "" : getDescription(), getPermission(), aliases == null ? new String[0] : aliases, (LiteralExecutor<S>) executor);
        getChildren().forEach(commandBuilder -> command.getChildren().add(commandBuilder.build()));
        getLiterals().forEach(commandBuilder -> command.getLiterals().add(commandBuilder.build()));
        getArguments().forEach(commandBuilder -> command.getArguments().add(commandBuilder.build()));

        return command;
    }
}