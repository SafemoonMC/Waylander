package me.eduardwayland.mooncraft.waylander.command.wrapper;

import lombok.SneakyThrows;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

final class Reflections {

    /*
    Constants
     */
    private static final @NotNull String SERVER_VERSION;
    private static final @NotNull String NMS_CLASS;
    private static final @NotNull String OBC_CLASS;

    /*
    Initializer
     */
    static {
        Class<?> server = Bukkit.getServer().getClass();
        String version = server.getName().substring("org.bukkit.craftbukkit".length());
        SERVER_VERSION = version.substring(0, version.length() - "CraftServer".length());
        NMS_CLASS = "net.minecraft.server" + SERVER_VERSION;
        OBC_CLASS = "org.bukkit.craftbukkit" + SERVER_VERSION;
    }

    /*
    Methods
     */
    @SneakyThrows
    public static @NotNull Class<?> getNMS(@NotNull String className) {
        return Class.forName(NMS_CLASS + className);
    }

    @SneakyThrows
    public static @NotNull Class<?> getOBC(@NotNull String className) {
        return Class.forName(OBC_CLASS + className);
    }
}