package me.eduardwayland.mooncraft.waylander.command.wrapper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.RootCommandNode;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BrigadierListener implements Listener {

    /*
    Fields
     */
    private final @NotNull Brigadier brigadier;

    /*
    Constructor
     */
    public BrigadierListener(@NotNull Brigadier brigadier) {
        this.brigadier = brigadier;
    }

    /*
    Handlers
     */
    @EventHandler
    public void on(@NotNull ServerLoadEvent e) {
        brigadier.refreshDispatcher();
        CommandDispatcher<?> dispatcher = brigadier.commandDispatcher;
        RootCommandNode<?> root = dispatcher.getRoot();

        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            for (BrigadierCommandPair brigadierCommandPair : brigadier.getBrigadierCommandWrapper().getCommandPairMap().values()) {
                brigadier.getBrigadierCommandWrapper().delChild(root, brigadierCommandPair.getCommandNode().getName());
                brigadier.getBrigadierCommandWrapper().addChild(brigadierCommandPair.getCommandNode());
            }
        } else {
            Bukkit.getServer().getOnlinePlayers().forEach(Player::updateCommands);
        }
    }

    @EventHandler
    public void on(@NotNull PlayerCommandSendEvent e) {
        List<String> minecraftPrefixedCommands = brigadier.getBrigadierCommandWrapper().getCommandPairMap().values().stream().map(BrigadierCommandPair::getMinecraftCommand).collect(Collectors.toList());
        e.getCommands().removeAll(minecraftPrefixedCommands);

        for (BrigadierCommandPair brigadierCommandPair : brigadier.getBrigadierCommandWrapper().getCommandPairMap().values()) {
            e.getCommands().remove(brigadierCommandPair.getMinecraftCommand());

            if (!brigadierCommandPair.getCommand().hasPermission(e.getPlayer())) {
                Arrays.stream(brigadierCommandPair.getCommand().getAliases()).forEach(alias -> e.getCommands().remove(alias));
            }
        }
    }
}