package me.eduardwayland.mooncraft.waylander.database.queries;

import lombok.RequiredArgsConstructor;

import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

public final class SingleQuery extends Query {

    /*
    Fields
     */
    private final @NotNull LinkedList<Object> parametersList;

    /*
    Constructor
     */
    private SingleQuery(@NotNull String query, @NotNull LinkedList<Object> parametersList) {
        super(query);
        this.parametersList = parametersList;
    }

    /*
    Override Methods
     */
    @Override
    public void updatePreparedStatement(@NotNull PreparedStatement preparedStatement) throws SQLException {
        if (parametersList.isEmpty()) return;
        for (int i = 1; i <= parametersList.size(); i++)
            preparedStatement.setObject(i, parametersList.get(i - 1));
    }

    /*
    Methods
     */
    public @NotNull LinkedList<Object> getParametersList() {
        return new LinkedList<>(parametersList);
    }

    /*
    Builder
     */
    @RequiredArgsConstructor
    public static class Builder {

        /*
        Fields
         */
        private final String query;
        private final LinkedList<Object> parametersList = new LinkedList<>();

        /*
        Methods
         */
        public Builder with(Object object) {
            parametersList.add(object);
            return this;
        }

        public SingleQuery build() {
            if (query == null) return null;
            return new SingleQuery(query, parametersList);
        }
    }
}