package me.eduardwayland.mooncraft.waylander.command.wrapper;

import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.command.LiteralCommand;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
final class BrigadierCommandPair {

    /*
    Fields
     */
    private final LiteralCommand command;
    private final LiteralCommandNode<Object> commandNode;

    /*
    Methods
     */
    @NotNull
    public String getMinecraftCommand() {
        return "minecraft:" + command.getName();
    }
}