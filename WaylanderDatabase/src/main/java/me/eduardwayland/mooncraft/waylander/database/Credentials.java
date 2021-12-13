package me.eduardwayland.mooncraft.waylander.database;

import lombok.Getter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
public class Credentials {

    /*
    Fields
     */
    private final @NotNull String hostname;
    private final @NotNull String port;
    private final @Nullable String database;
    private final @NotNull String username;
    private final @NotNull String password;
    private final int maxPoolSize;
    private final int minIdleConnections;
    private final int maxLifetime;
    private final int keepAliveTime;
    private final int connectionTimeout;
    private final @NotNull Map<String, String> properties;

    /*
    Constructor
     */
    public Credentials(@NotNull String hostname, @NotNull String port, @Nullable String database, @NotNull String username, @NotNull String password, int maxPoolSize, int minIdleConnections, int maxLifetime, int keepAliveTime, int connectionTimeout, @NotNull Map<String, String> properties) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.minIdleConnections = minIdleConnections;
        this.maxLifetime = maxLifetime;
        this.keepAliveTime = keepAliveTime;
        this.connectionTimeout = connectionTimeout;
        this.properties = properties;
    }
}