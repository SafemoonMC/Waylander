package me.eduardwayland.mooncraft.waylander.command.builders;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;

import me.eduardwayland.mooncraft.waylander.command.RequiredCommand;
import me.eduardwayland.mooncraft.waylander.command.executor.RequiredExecutor;
import me.eduardwayland.mooncraft.waylander.command.suggest.Suggestion;
import me.eduardwayland.mooncraft.waylander.command.suggest.Suggestions;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequiredCommandBuilder<S extends CommandSender, A, T> extends CommandBuilder<S, RequiredCommandBuilder<S, A, ?>> {

    /*
    Static Methods
     */
    @NotNull
    public static <S extends CommandSender, A> RequiredCommandBuilder<S, A, ?> name(@NotNull String name, @NotNull ArgumentType<A> argumentType) {
        return new RequiredCommandBuilder<>(name, argumentType);
    }

    /*
    Fields
     */
    /*
    Fields
     */
    private @NotNull
    final ArgumentType<A> argumentType;
    private @Nullable Suggestions suggestions;
    private @Nullable RequiredExecutor<S> executor;

    /*
    Constructor
     */
    protected RequiredCommandBuilder(@NotNull String name, @NotNull ArgumentType<A> argumentType) {
        super(name);
        this.argumentType = argumentType;
    }

    /*
    Methods
     */
    @NotNull
    public RequiredCommandBuilder<S, A, T> suggests(@NotNull String argument, @NotNull String tooltip) {
        if (suggestions == null) suggestions = new Suggestions();
        suggestions.add(new Suggestion(argument, new LiteralMessage(tooltip)));
        return getThis();
    }

    public RequiredCommandBuilder<S, A, T> executes(@NotNull RequiredExecutor<S> executor) {
        this.executor = executor;
        return getThis();
    }

    /*
    Override Methods
     */
    @Override
    protected RequiredCommandBuilder<S, A, T> getThis() {
        return this;
    }

    @Override
    public RequiredCommand<S, A> build() {
        RequiredCommand<S, A> command = new RequiredCommand<>(getName(), getDescription() == null ? "" : getDescription(), getPermission(), argumentType, suggestions, executor);
        getChildren().forEach(commandBuilder -> command.getChildren().add(commandBuilder.build()));
        getLiterals().forEach(commandBuilder -> command.getLiterals().add(commandBuilder.build()));
        getArguments().forEach(commandBuilder -> command.getArguments().add(commandBuilder.build()));

        return command;
    }
}