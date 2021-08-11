package me.eduardwayland.mooncraft.waylander.database.connection.hikari.impl;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.database.Credentials;
import me.eduardwayland.mooncraft.waylander.database.connection.hikari.HikariConnectionFactory;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

@Getter
public class MySQLConnectionFactory extends HikariConnectionFactory {
    
    /*
    Fields
     */
    private final String poolName;
    
    /*
    Constructor
     */
    public MySQLConnectionFactory(String poolName, Credentials credentials) {
        super(credentials);
        this.poolName = poolName;
    }
    
    /*
    Override Methods
     */
    @Override
    public void configureDatabase(HikariConfig hikariConfig, String address, String port, String databaseName, String username, String password) {
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + databaseName);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setPoolName(poolName);
    }
    
    @Override
    public void postInit() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals("com.mysql.cj.jdbc.Driver")) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ignored) {
                }
            }
        }
    }
    
    @Override
    protected void overrideProperties(Map<String, String> propertiesMap) {
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        propertiesMap.putIfAbsent("cachePrepStmts", "true");
        propertiesMap.putIfAbsent("prepStmtCacheSize", "250");
        propertiesMap.putIfAbsent("prepStmtCacheSqlLimit", "2048");
        propertiesMap.putIfAbsent("useServerPrepStmts", "true");
        propertiesMap.putIfAbsent("useLocalSessionState", "true");
        propertiesMap.putIfAbsent("rewriteBatchedStatements", "true");
        propertiesMap.putIfAbsent("cacheResultSetMetadata", "true");
        propertiesMap.putIfAbsent("cacheServerConfiguration", "true");
        propertiesMap.putIfAbsent("elideSetAutoCommits", "true");
        propertiesMap.putIfAbsent("maintainTimeStats", "false");
        propertiesMap.putIfAbsent("alwaysSendSetIsolation", "false");
        propertiesMap.putIfAbsent("cacheCallableStmts", "true");
        propertiesMap.putIfAbsent("serverTimezone", "UTC");
        
        super.overrideProperties(propertiesMap);
    }
}