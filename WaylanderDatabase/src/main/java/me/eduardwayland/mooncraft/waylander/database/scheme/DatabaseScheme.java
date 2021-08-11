package me.eduardwayland.mooncraft.waylander.database.scheme;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.database.queries.Query;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public abstract class DatabaseScheme {
    
    /*
    Fields
     */
    private final @NotNull String name;
    private final @NotNull LinkedList<Query> queryList;
}