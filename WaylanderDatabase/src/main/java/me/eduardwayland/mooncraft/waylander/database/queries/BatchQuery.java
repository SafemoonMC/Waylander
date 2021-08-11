package me.eduardwayland.mooncraft.waylander.database.queries;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

public final class BatchQuery extends Query {
    
    /*
    Fields
     */
    private final @NotNull LinkedList<Object[]> parametersList;
    
    /*
    Constructor
     */
    protected BatchQuery(@NotNull String query, @NotNull LinkedList<Object[]> parametersList) {
        super(query);
        this.parametersList = parametersList;
    }
    
    /*
    Override Methods
     */
    @Override
    public void updatePreparedStatement(@NotNull PreparedStatement preparedStatement) throws SQLException {
        if (parametersList.isEmpty()) return;
        for (Object[] object : parametersList) {
            for (int i = 1; i <= object.length; i++) {
                preparedStatement.setObject(i, object[i - 1]);
            }
            preparedStatement.addBatch();
        }
    }
    
    /*
    Methods
     */
    public LinkedList<Object[]> getParametersList() {
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
        private final LinkedList<Object[]> parametersList = new LinkedList<>();
        
        /*
        Methods
         */
        public Builder with(@NotNull Object... objects) {
            parametersList.add(objects);
            return this;
        }
        
        public BatchQuery build() {
            if (query == null) return null;
            return new BatchQuery(query, parametersList);
        }
    }
}