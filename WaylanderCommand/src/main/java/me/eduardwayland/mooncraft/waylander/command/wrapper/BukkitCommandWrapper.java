package me.eduardwayland.mooncraft.waylander.command.wrapper;

import com.mojang.brigadier.CommandDispatcher;
import me.eduardwayland.mooncraft.waylander.command.LiteralCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class BukkitCommandWrapper {

    /*
    Fields
     */
    private final @NotNull Plugin plugin;
    @Nullable CommandDispatcher<Object> commandDispatcher;

    /*
    Constructor
     */
    public BukkitCommandWrapper(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /*
    Methods
     */
    public void register(@NotNull LiteralCommand<?> literalCommand) {
        if (commandDispatcher == null)
            throw new IllegalStateException("The command cannot be registered. CommandDispatcher instance is missing.");
        DispatcherCommand dispatcherCommand = new DispatcherCommand(plugin, commandDispatcher, literalCommand.getName(), literalCommand.getDescription(), "", Arrays.asList(literalCommand.getAliases()));
        Bukkit.getCommandMap().register(plugin.getName().toLowerCase(), dispatcherCommand);
    }

    public void unregisterAll() {
        for (Map.Entry<String, Command> entry : new ArrayList<>(Bukkit.getCommandMap().getKnownCommands().entrySet())) {
            entry.getValue().unregister(Bukkit.getCommandMap());
            Bukkit.getCommandMap().getKnownCommands().remove(entry.getKey());
        }
    }
}