package me.eduardwayland.mooncraft.waylander.command.wrapper;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BrigadierExceptions {

    /*
    Constants
     */
    private static final Method SEND_FAILURE;
    private static final Method FROM_MESSAGE;

    static {
        final Class<?> VANILLA_COMMAND_WRAPPER = Reflections.getOBC("command.VanillaCommandWrapper");
        final Class<?> I_CHAT_BASE_COMPONENT = Reflections.getNMS("IChatBaseComponent");
        final Class<?> CHAT_COMPONENT_UTILS = Reflections.getNMS("ChatComponentUtils");

        try {
            SEND_FAILURE = VANILLA_COMMAND_WRAPPER.getDeclaredMethod("sendFailure", I_CHAT_BASE_COMPONENT);
            FROM_MESSAGE = CHAT_COMPONENT_UTILS.getDeclaredMethod("fromMessage", Message.class);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /*
    Static Methods
     */
    static void report(@NotNull CommandSender sender, @NotNull CommandSyntaxException exception) {
        sendFailure(sender, exception.getRawMessage());
    }

    static void sendFailure(@NotNull CommandSender commandSender, @NotNull Message message) {
        try {
            var vanillaCommandWrapper = Brigadier.getVanillaCommandListener(commandSender);
            var chatBaseComponent = FROM_MESSAGE.invoke(null, message);
            SEND_FAILURE.invoke(vanillaCommandWrapper, chatBaseComponent);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}