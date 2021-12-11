package me.eduardwayland.mooncraft.waylander.database.entities;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface EntityParent<T> {

    @NotNull CompletableFuture<T> withChildren();
}