package me.eduardwayland.mooncraft.waylander.database.connection.hikari.impl;

import com.zaxxer.hikari.HikariConfig;

import me.eduardwayland.mooncraft.waylander.database.Credentials;
import me.eduardwayland.mooncraft.waylander.database.connection.hikari.HikariConnectionFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

public class MariaDBConnectionFactory extends HikariConnectionFactory {

    /*
    Fields
     */
    private final @NotNull String poolName;

    /*
    Constructor
     */
    public MariaDBConnectionFactory(@NotNull String poolName, @NotNull Credentials credentials) {
        super(credentials);
        this.poolName = poolName;
    }

    /*
    Override Methods
     */
    @Override
    public void configureDatabase(@NotNull HikariConfig hikariConfig, @NotNull String address, @NotNull String port, @Nullable String databaseName, @NotNull String username, @NotNull String password) {
        hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + address + ":" + port + "/" + (databaseName != null ? databaseName : ""));
        hikariConfig.addDataSourceProperty("serverName", address);
        hikariConfig.addDataSourceProperty("port", port);
        hikariConfig.addDataSourceProperty("databaseName", databaseName);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setPoolName(poolName);
    }

    @Override
    public void postInit() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals("org.mariadb.jdbc.Driver")) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    @Override
    protected void overrideProperties(@NotNull Map<String, String> propertiesMap) {
        // https://mariadb.com/kb/en/about-mariadb-connector-j/
        propertiesMap.putIfAbsent("useServerPrepStmts", "true");
        propertiesMap.putIfAbsent("rewriteBatchedStatements", "true");
        propertiesMap.putIfAbsent("serverTimezone", "UTC");

        super.overrideProperties(propertiesMap);
    }

    @Override
    protected void setProperties(@NotNull HikariConfig hikariConfig, @NotNull Map<String, String> properties) {
        String propertiesString = properties.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        hikariConfig.addDataSourceProperty("properties", propertiesString);
    }
}