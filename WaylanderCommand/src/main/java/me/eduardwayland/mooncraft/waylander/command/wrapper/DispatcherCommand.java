package me.eduardwayland.mooncraft.waylander.command.wrapper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Getter
public final class DispatcherCommand extends Command implements PluginIdentifiableCommand {

    /*
    Fields
     */
    private final @NotNull Plugin plugin;
    private final @NotNull CommandDispatcher<Object> dispatcher;

    /*
    Constructor
     */
    public DispatcherCommand(@NotNull Plugin plugin, @NotNull CommandDispatcher<Object> dispatcher, @NotNull String name, @Nullable String description, @Nullable String usage, @Nullable List<String> aliases) {
        super(name, description == null ? "" : description, usage == null ? "" : usage, aliases == null ? Collections.emptyList() : aliases);
        this.plugin = plugin;
        this.dispatcher = dispatcher;
    }

    /*
    Override Methods
     */
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) return true;
        var command = join(commandLabel, args);
        var reader = new StringReader(command);
        if (reader.canRead() && reader.peek() == '/') reader.skip();

        try {
            dispatcher.execute(reader, Brigadier.getVanillaCommandListener(sender));
        } catch (CommandSyntaxException e) {
            BrigadierExceptions.report(sender, e);
            return true;
        } catch (Exception e) {
            plugin.getLogger().info("Command Exception: " + e);
            return true;
        }
        return true;
    }

    /*
    Methods
     */
    private String join(@NotNull String name, String @NotNull [] arguments) {
        String command = "/" + name;
        if (arguments.length > 0) command += " " + String.join(" ", arguments);
        return command;
    }
}