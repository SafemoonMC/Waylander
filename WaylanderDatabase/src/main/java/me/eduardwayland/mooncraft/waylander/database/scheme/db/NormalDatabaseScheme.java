package me.eduardwayland.mooncraft.waylander.database.scheme.db;

import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.database.queries.Query;
import me.eduardwayland.mooncraft.waylander.database.scheme.DatabaseScheme;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

@Getter
public final class NormalDatabaseScheme extends DatabaseScheme {
    
    /*
    Constructor
     */
    public NormalDatabaseScheme(@NotNull String name, @NotNull LinkedList<Query> queryList) {
        super(name, queryList);
    }
}