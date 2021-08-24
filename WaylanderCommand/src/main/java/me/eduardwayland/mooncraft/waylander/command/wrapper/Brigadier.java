package me.eduardwayland.mooncraft.waylander.command.wrapper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import me.eduardwayland.mooncraft.waylander.command.LiteralCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public final class Brigadier {

    /*
    Constants
     */
    private static final Field CONSOLE_FIELD;
    private static final Field COMMAND_ARGUMENTS_FIELD;
    private static final Method GET_COMMAND_DISPATCHER_METHOD;
    private static final Method GET_BUKKIT_SENDER_METHOD;
    private static final Method GET_BRIGADIER_DISPATCHER_METHOD;
    private static final Field CHILDREN_FIELD;
    private static final Field LITERALS_FIELD;
    private static final Field ARGUMENTS_FIELD;
    private static final Field[] CHILDREN_FIELDS;

    /*
    Initializer
     */
    static {
        final Class<?> minecraftServer = Reflections.getNMS("MinecraftServer");
        final Class<?> commandListenerWrapper = Reflections.getNMS("CommandListenerWrapper");
        final Class<?> commandDispatcher = Reflections.getNMS("CommandDispatcher");
        final Class<?> craftServer = Reflections.getOBC("CraftServer");

        try {
            CONSOLE_FIELD = craftServer.getDeclaredField("console");
            CONSOLE_FIELD.setAccessible(true);
            COMMAND_ARGUMENTS_FIELD = CommandContext.class.getDeclaredField("arguments");
            COMMAND_ARGUMENTS_FIELD.setAccessible(true);

            GET_COMMAND_DISPATCHER_METHOD = minecraftServer.getDeclaredMethod("getCommandDispatcher");
            GET_COMMAND_DISPATCHER_METHOD.setAccessible(true);

            GET_BUKKIT_SENDER_METHOD = commandListenerWrapper.getDeclaredMethod("getBukkitSender");
            GET_BUKKIT_SENDER_METHOD.setAccessible(true);

            GET_BRIGADIER_DISPATCHER_METHOD = Arrays.stream(commandDispatcher.getDeclaredMethods())
                    .filter(method -> method.getParameterCount() == 0)
                    .filter(method -> CommandDispatcher.class.isAssignableFrom(method.getReturnType()))
                    .findFirst().orElseThrow(NoSuchMethodException::new);
            GET_BRIGADIER_DISPATCHER_METHOD.setAccessible(true);

            CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            CHILDREN_FIELDS = new Field[]{CHILDREN_FIELD, LITERALS_FIELD, ARGUMENTS_FIELD};
            Arrays.stream(CHILDREN_FIELDS).forEach(Field::trySetAccessible);
            for (Field field : CHILDREN_FIELDS) {
                field.setAccessible(true);
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /*
    Fields
     */
    private final Map<String, BrigadierCommandPair> commandMap = new HashMap<>();

    /*
    Constructor
     */
    public Brigadier(JavaPlugin javaPlugin) {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            @SuppressWarnings({"rawtypes", "unchecked"})
            public void onLoad(ServerLoadEvent e) {
                CommandDispatcher dispatcher = getDispatcher();
                RootCommandNode root = dispatcher.getRoot();

                for (BrigadierCommandPair brigadierCommandPair : getCommandMap().values()) {
                    delChild(root, brigadierCommandPair.getCommandNode().getName());
                    root.addChild(brigadierCommandPair.getCommandNode());
                }
            }

            @EventHandler
            public void onCommandSend(PlayerCommandSendEvent e) {
                List<String> minecraftPrefixedCommands = commandMap.values().stream().map(BrigadierCommandPair::getMinecraftCommand).collect(Collectors.toList());
                e.getCommands().removeAll(minecraftPrefixedCommands);

                for (BrigadierCommandPair brigadierCommandPair : getCommandMap().values()) {
                    e.getCommands().remove(brigadierCommandPair.getMinecraftCommand());

                    if (!brigadierCommandPair.getCommand().hasPermission(e.getPlayer())) {
                        Arrays.stream(brigadierCommandPair.getCommand().getAliases()).forEach(alias -> e.getCommands().remove(alias));
                    }
                }
            }
        }, javaPlugin);
    }

    /*
    Static Methods
     */
    @NotNull
    public static CommandSender getBukkitSender(@NotNull Object commandWrapperListener) {
        try {
            return (CommandSender) GET_BUKKIT_SENDER_METHOD.invoke(commandWrapperListener);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static Map<String, ParsedArgument<Object, ?>> getArguments(@NotNull CommandContext<Object> commandContext) {
        try {
            return (Map<String, ParsedArgument<Object, ?>>) COMMAND_ARGUMENTS_FIELD.get(commandContext);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Methods
     */
    public void register(LiteralCommand<?> literalCommand, boolean createHelpCommand) {
        LiteralCommandNode<Object> literalCommandNode = literalCommand.toLiteralArgumentBuilder(createHelpCommand).build();

        // Register main command
        addChild(literalCommandNode);
        commandMap.put(literalCommand.getName(), new BrigadierCommandPair(literalCommand, literalCommandNode));

        // Register main's aliases
        Arrays.stream(literalCommand.getAliases()).forEach(alias -> {
            addChild(LiteralArgumentBuilder.literal(alias).redirect(literalCommandNode).executes(literalCommandNode.getCommand()).build());
            commandMap.put(alias, new BrigadierCommandPair(literalCommand, literalCommandNode));
        });

        Bukkit.getLogger().info("Command registered through Waylander: " + literalCommand.getName());
    }

    @NotNull
    public CommandDispatcher<?> getDispatcher() {
        try {
            Object minecraftServer = CONSOLE_FIELD.get(Bukkit.getServer());
            Object commandDispatcher = GET_COMMAND_DISPATCHER_METHOD.invoke(minecraftServer);
            return (CommandDispatcher<?>) GET_BRIGADIER_DISPATCHER_METHOD.invoke(commandDispatcher);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, BrigadierCommandPair> getCommandMap() {
        return Collections.unmodifiableMap(commandMap);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addChild(LiteralCommandNode<Object> literalCommandNode) {
        CommandDispatcher dispatcher = getDispatcher();
        RootCommandNode rootCommandNode = dispatcher.getRoot();

        delChild(rootCommandNode, literalCommandNode.getName());
        rootCommandNode.addChild(literalCommandNode);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void delChild(RootCommandNode root, String name) {
        try {
            for (Field field : CHILDREN_FIELDS) {
                Map<String, ?> children = (Map<String, ?>) field.get(root);
                children.remove(name);
                field.set(root, children);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}