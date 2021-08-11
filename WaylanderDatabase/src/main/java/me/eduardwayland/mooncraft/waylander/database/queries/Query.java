package me.eduardwayland.mooncraft.waylander.database.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
@ToString
@AllArgsConstructor
public abstract class Query {
    
    /*
    Fiels
     */
    private final @NotNull String query;
    
    /*
    Abstract Methods
     */
    public abstract void updatePreparedStatement(@NotNull PreparedStatement preparedStatement) throws SQLException;
    
    /*
    Builder
     */
    public static SingleQuery.Builder single(@NotNull String query) {
        return new SingleQuery.Builder(query);
    }
    
    public static BatchQuery.Builder batch(@NotNull String query) {
        return new BatchQuery.Builder(query);
    }
}