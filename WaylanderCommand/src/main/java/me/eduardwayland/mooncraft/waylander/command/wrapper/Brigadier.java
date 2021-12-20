package me.eduardwayland.mooncraft.waylander.command.wrapper;

import lombok.AccessLevel;
import lombok.Getter;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public final class Brigadier {

    /*
    Constants
     */
    private static final Field CONSOLE_FIELD;
    private static final Field COMMAND_ARGUMENTS_FIELD;
    private static final Method GET_COMMAND_DISPATCHER_METHOD;
    private static final Method GET_BUKKIT_SENDER_METHOD;
    private static final Method GET_BRIGADIER_DISPATCHER_METHOD;
    private static final Method GET_LISTENER;

    /*
    Initializer
     */
    static {
        final Class<?> craftServer = Reflections.getOBC("CraftServer");
        final Class<?> minecraftServer = Reflections.getNMS("server.MinecraftServer");
        final Class<?> commandDispatcher = Reflections.getNMS("commands.CommandDispatcher");
        final Class<?> commandListenerWrapper = Reflections.getNMS("commands.CommandListenerWrapper");
        final Class<?> VANILLA_COMMAND_WRAPPER = Reflections.getOBC("command.VanillaCommandWrapper");

        try {
            CONSOLE_FIELD = craftServer.getDeclaredField("console");
            CONSOLE_FIELD.setAccessible(true);

            GET_COMMAND_DISPATCHER_METHOD = minecraftServer.getDeclaredMethod("getCommandDispatcher");
            GET_COMMAND_DISPATCHER_METHOD.setAccessible(true);

            GET_BUKKIT_SENDER_METHOD = commandListenerWrapper.getDeclaredMethod("getBukkitSender");
            GET_BUKKIT_SENDER_METHOD.setAccessible(true);

            GET_BRIGADIER_DISPATCHER_METHOD = Arrays.stream(commandDispatcher.getDeclaredMethods())
                    .filter(method -> method.getParameterCount() == 0)
                    .filter(method -> CommandDispatcher.class.isAssignableFrom(method.getReturnType()))
                    .findFirst().orElseThrow(NoSuchMethodException::new);
            GET_BRIGADIER_DISPATCHER_METHOD.setAccessible(true);

            GET_LISTENER = VANILLA_COMMAND_WRAPPER.getDeclaredMethod("getListener", CommandSender.class);

            COMMAND_ARGUMENTS_FIELD = CommandContext.class.getDeclaredField("arguments");
            COMMAND_ARGUMENTS_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
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

    public static Object getVanillaCommandListener(@NotNull CommandSender commandSender) {
        try {
            return GET_LISTENER.invoke(null, commandSender);
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
    Fields
     */
    @Getter(value = AccessLevel.PACKAGE)
    private final @NotNull BukkitCommandWrapper bukkitCommandWrapper;
    @Getter
    private final @NotNull BrigadierCommandWrapper brigadierCommandWrapper;
    CommandDispatcher<Object> commandDispatcher;

    /*
    Constructor
     */
    public Brigadier(@NotNull Plugin plugin) throws NoSuchFieldException {
        this.bukkitCommandWrapper = new BukkitCommandWrapper(plugin);
        this.brigadierCommandWrapper = new BrigadierCommandWrapper(plugin);
        refreshDispatcher();

        Bukkit.getPluginManager().registerEvents(new BrigadierListener(this), plugin);
    }


    /*
    Methods
     */
    public void refreshDispatcher() {
        this.commandDispatcher = getDispatcher();
        this.bukkitCommandWrapper.commandDispatcher = this.commandDispatcher;
        this.brigadierCommandWrapper.commandDispatcher = this.commandDispatcher;
    }

    /*
    Methods
     */
    @NotNull
    @SuppressWarnings("unchecked")
    private CommandDispatcher<Object> getDispatcher() {
        try {
            Object minecraftServer = CONSOLE_FIELD.get(Bukkit.getServer());
            Object commandDispatcher = GET_COMMAND_DISPATCHER_METHOD.invoke(minecraftServer);
            return (CommandDispatcher<Object>) GET_BRIGADIER_DISPATCHER_METHOD.invoke(commandDispatcher);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}