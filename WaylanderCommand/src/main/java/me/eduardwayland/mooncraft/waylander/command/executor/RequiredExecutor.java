package me.eduardwayland.mooncraft.waylander.command.executor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.eduardwayland.mooncraft.waylander.command.executor.arguments.Arguments;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface RequiredExecutor<S extends CommandSender> {

    void onExecute(@NotNull S sender, Arguments arguments) throws CommandSyntaxException;

}