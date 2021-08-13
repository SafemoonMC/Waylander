package me.eduardwayland.mooncraft.waylander.database.connection.hikari.impl;

import com.zaxxer.hikari.HikariConfig;
import me.eduardwayland.mooncraft.waylander.database.Credentials;
import me.eduardwayland.mooncraft.waylander.database.connection.hikari.HikariConnectionFactory;

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
    private final String poolName;
    
    /*
    Constructor
     */
    public MariaDBConnectionFactory(String poolName, Credentials credentials) {
        super(credentials);
        this.poolName = poolName;
    }
    
    /*
    Override Methods
     */
    @Override
    public void configureDatabase(HikariConfig hikariConfig, String address, String port, String databaseName, String username, String password) {
        hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + address + ":" + port + "/" + databaseName);
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
    protected void overrideProperties(Map<String, String> propertiesMap) {
        // https://mariadb.com/kb/en/about-mariadb-connector-j/
        propertiesMap.putIfAbsent("useServerPrepStmts", "true");
        propertiesMap.putIfAbsent("rewriteBatchedStatements", "true");
        propertiesMap.putIfAbsent("serverTimezone", "UTC");
        
        super.overrideProperties(propertiesMap);
    }
    
    @Override
    protected void setProperties(HikariConfig hikariConfig, Map<String, String> properties) {
        String propertiesString = properties.entrySet()
                                            .stream()
                                            .map(e -> e.getKey() + "=" + e.getValue())
                                            .collect(Collectors.joining("&"));
        hikariConfig.addDataSourceProperty("properties", propertiesString);
    }
}