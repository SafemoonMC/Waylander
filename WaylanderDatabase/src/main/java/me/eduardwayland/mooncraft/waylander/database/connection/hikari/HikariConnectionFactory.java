package me.eduardwayland.mooncraft.waylander.database.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.eduardwayland.mooncraft.waylander.database.Credentials;
import me.eduardwayland.mooncraft.waylander.database.connection.ConnectionFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class HikariConnectionFactory implements ConnectionFactory {

    /*
    Fields
     */
    private final @NotNull Credentials credentials;
    private HikariDataSource hikariDataSource;

    /*
    Constructor
     */
    public HikariConnectionFactory(@NotNull Credentials credentials) {
        this.credentials = credentials;
    }

    /*
    Override Methods
     */
    @Override
    public void init() {
        HikariConfig hikariConfig = new HikariConfig();

        // Configure the HikariConfig
        configureDatabase(hikariConfig, credentials.getHostname(), credentials.getPort(), credentials.getDatabase(), credentials.getUsername(), credentials.getPassword());

        // Override current connection properties from the configuration
        Map<String, String> properties = new HashMap<>(this.credentials.getProperties());
        overrideProperties(properties);

        // Set the final properties
        setProperties(hikariConfig, properties);

        // Configure connection pool
        hikariConfig.setMaximumPoolSize(this.credentials.getMaxPoolSize());
        hikariConfig.setMinimumIdle(this.credentials.getMinIdleConnections());
        hikariConfig.setMaxLifetime(this.credentials.getMaxLifetime());
        hikariConfig.setKeepaliveTime(this.credentials.getKeepAliveTime());
        hikariConfig.setConnectionTimeout(this.credentials.getConnectionTimeout());

        this.hikariDataSource = new HikariDataSource(hikariConfig);
        postInit();
    }

    @Override
    public void shutdown() {
        if (this.hikariDataSource == null) return;
        this.hikariDataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) {
            throw new SQLException("Unable to get a connection from the pool. HikariDataSource is not set.");
        }
        Connection connection = this.hikariDataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. HikariDataSource is not able to return a connection.");
        }
        return connection;
    }

    /*
    Methods
     */
    protected abstract void configureDatabase(@NotNull HikariConfig hikariConfig, @NotNull String address, @NotNull String port, @Nullable String databaseName, @NotNull String username, @NotNull String password);

    protected void overrideProperties(@NotNull Map<String, String> propertiesMap) {
        // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        propertiesMap.putIfAbsent("socketTime", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    protected void setProperties(@NotNull HikariConfig hikariConfig, @NotNull Map<String, String> properties) {
        properties.forEach(hikariConfig::addDataSourceProperty);
    }
}