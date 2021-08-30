package me.eduardwayland.mooncraft.waylander.command.wrapper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.command.LiteralCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class BrigadierCommandWrapper {

    /*
    Fields
     */
    private final @NotNull Plugin plugin;
    @Getter
    private final @NotNull Map<String, BrigadierCommandPair> commandPairMap;
    private final @NotNull Field[] childrenFields;

    @Nullable CommandDispatcher<Object> commandDispatcher;

    /*
    Constructor
     */
    public BrigadierCommandWrapper(@NotNull Plugin plugin) throws NoSuchFieldException {
        this.plugin = plugin;
        this.commandPairMap = new HashMap<>();
        this.childrenFields = new Field[]{CommandNode.class.getDeclaredField("children"), CommandNode.class.getDeclaredField("literals"), CommandNode.class.getDeclaredField("arguments")};
        Arrays.stream(this.childrenFields).forEach(Field::trySetAccessible);
    }

    /*
    Methods
     */
    public void register(@NotNull LiteralCommand<?> literalCommand) {
        register(literalCommand, false);
    }

    public void register(@NotNull LiteralCommand<?> literalCommand, boolean createHelpCommand) {
        if (commandDispatcher == null) throw new IllegalStateException("Child cannot be added. CommandDispatcher instance is missing.");

        LiteralCommandNode<Object> literalCommandNode = literalCommand.toLiteralArgumentBuilder(createHelpCommand).build();

        // Register main command
        addChild(literalCommandNode);
        commandPairMap.put(literalCommand.getName(), new BrigadierCommandPair(literalCommand, literalCommandNode));

        // Register main's aliases
        Arrays.stream(literalCommand.getAliases()).forEach(alias -> {
            addChild(LiteralArgumentBuilder.<Object>literal(alias).redirect(literalCommandNode).executes(literalCommandNode.getCommand()).build());
            commandPairMap.put(alias, new BrigadierCommandPair(literalCommand, literalCommandNode));
        });

        plugin.getLogger().info("A new command has been registered through WaylanderCommand: " + literalCommand.getName());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void addChild(@NotNull LiteralCommandNode<Object> literalCommandNode) {
        if (commandDispatcher == null) throw new IllegalStateException("Child cannot be added. CommandDispatcher instance is missing.");
        CommandDispatcher dispatcher = commandDispatcher;
        RootCommandNode rootCommandNode = dispatcher.getRoot();

        delChild(rootCommandNode, literalCommandNode.getName());
        rootCommandNode.addChild(literalCommandNode);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    void delChild(@NotNull RootCommandNode root, @NotNull String name) {
        try {
            for (Field field : childrenFields) {
                Map<String, ?> children = (Map<String, ?>) field.get(root);
                children.remove(name);
                field.set(root, children);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}