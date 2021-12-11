package me.eduardwayland.mooncraft.waylander.database.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    void init();

    void postInit();

    void shutdown() throws SQLException;

    Connection getConnection() throws SQLException;
}