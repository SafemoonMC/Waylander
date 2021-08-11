package me.eduardwayland.mooncraft.waylander.database.entities;

import java.util.concurrent.CompletableFuture;

public interface EntityParent<T> {
    
    
    CompletableFuture<T> withChildren();
}