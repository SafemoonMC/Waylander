package me.eduardwayland.mooncraft.waylander.command.executor;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface LiteralExecutor<S extends CommandSender> {

    void onExecute(@NotNull S sender);
}