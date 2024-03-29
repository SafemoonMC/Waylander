package me.eduardwayland.mooncraft.waylander.database;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import me.eduardwayland.mooncraft.waylander.database.queries.BatchQuery;
import me.eduardwayland.mooncraft.waylander.database.queries.Query;
import me.eduardwayland.mooncraft.waylander.database.resultset.ResultSetIterator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class DatabaseManager {

    /*
    Fields
     */
    private final @NotNull Database database;

    /*
    Methods
     */
    public <T> @NotNull CompletableFuture<T> executeQuery(@NotNull Query query, @NotNull Function<ResultSetIterator, T> function) {
        return CompletableFuture.supplyAsync(() -> {
            T object = null;
            long start = System.currentTimeMillis();
            try (Connection connection = database.getConnectionFactory().getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(query.getQuery(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    query.updatePreparedStatement(preparedStatement);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        ResultSetIterator resultSetIterator = new ResultSetIterator(resultSet);
                        object = function.apply(resultSetIterator);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                long finish = System.currentTimeMillis();
                if (database.getSummaryStatistics() != null) {
                    database.getSummaryStatistics().accept(finish - start);
                }
            }
            return object;
        }, database.getScheduler().async());
    }

    public <T> @NotNull CompletableFuture<T> updateQuery(@NotNull Query query, @Nullable Function<Long, T> function, boolean returnGeneratedKeys) {
        return CompletableFuture.supplyAsync(() -> {
            T object = null;
            try (Connection connection = database.getConnectionFactory().getConnection()) {
                if (query instanceof BatchQuery) connection.setAutoCommit(false);
                try (PreparedStatement preparedStatement = returnGeneratedKeys ? connection.prepareStatement(query.getQuery(), Statement.RETURN_GENERATED_KEYS) : connection.prepareStatement(query.getQuery())) {
                    query.updatePreparedStatement(preparedStatement);

                    if (query instanceof BatchQuery) {
                        preparedStatement.executeBatch();
                        connection.commit();
                        connection.setAutoCommit(true);
                        object = function == null ? null : function.apply(null);
                    } else {
                        long update = preparedStatement.executeUpdate();
                        if (returnGeneratedKeys) {
                            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                                object = resultSet.next() ? function == null ? null : function.apply(resultSet.getLong(1)) : function == null ? null : function.apply(null);
                            }
                        } else object = function == null ? null : function.apply(update);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return object;
        }, database.getScheduler().async());
    }

    public <T> @NotNull CompletableFuture<T> updateQuery(@NotNull Query query, @Nullable Function<Long, T> function) {
        return updateQuery(query, function, false);
    }

    public @NotNull CompletableFuture<Void> updateQuery(@NotNull Query query) {
        return updateQuery(query, null);
    }

}