package me.eduardwayland.mooncraft.waylander.database.resultset;

import lombok.AllArgsConstructor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public final class ResultSetWrapper {

    /*
    Fields
     */
    private final @NotNull ResultSet resultSet;

    /*
    Methods
     */
    public <T> @Nullable T get(@NotNull String column, @NotNull Class<T> tClass) {
        try {
            return resultSet.getObject(column, tClass);
        } catch (SQLException throwables) {
            return null;
        }
    }
}