package me.eduardwayland.mooncraft.waylander.database.resultset;

import lombok.AllArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public final class ResultSetWrapper {
    
    /*
    Fields
     */
    private final ResultSet resultSet;
    
    /*
    Methods
     */
    public <T> T get(String column, Class<T> tClass) {
        try {
            return resultSet.getObject(column, tClass);
        } catch (SQLException throwables) {
            return null;
        }
    }
}