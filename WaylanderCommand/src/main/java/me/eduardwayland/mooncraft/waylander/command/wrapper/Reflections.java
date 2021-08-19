package me.eduardwayland.mooncraft.waylander.command.wrapper;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;

final class Reflections {

    /*
    Initializer
     */
    static {
        Class<?> server = Bukkit.getServer().getClass();
        String version = server.getName().substring("org.bukkit.craftbukkit".length());
        SERVER_VERSION = version.substring(0, version.length() - "CraftServer".length());
    }

    /*
    Constants
     */
    private static final String SERVER_VERSION;
    private static final String NMS_CLASS = "net.minecraft.server" + SERVER_VERSION;
    private static final String OBC_CLASS = "org.bukkit.craftbukkit" + SERVER_VERSION;


    /*
    Methods
     */
    @SneakyThrows
    public static Class<?> getNMS(String className) {
        return Class.forName(NMS_CLASS + className);
    }

    @SneakyThrows
    public static Class<?> getOBC(String className) {
        return Class.forName(OBC_CLASS + className);
    }
}