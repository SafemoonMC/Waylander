package me.eduardwayland.mooncraft.waylander.database.resultset;

import lombok.AllArgsConstructor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

@AllArgsConstructor
public final class ResultSetIterator {

    /*
    Fields
     */
    private final @NotNull ResultSet resultSet;

    /*
    Methods
     */
    public boolean hasNext() {
        try {
            boolean hasElement = resultSet.next();
            resultSet.previous();
            return hasElement;
        } catch (SQLException throwables) {
            return false;
        }
    }

    public @Nullable ResultSet next() {
        if (!hasNext()) return null;
        try {
            resultSet.next();
            return resultSet;
        } catch (SQLException throwables) {
            return null;
        }
    }

    public void forEachRemaining(@NotNull Consumer<? super ResultSet> action) {
        do {
            action.accept(next());
        } while (hasNext());
    }
}