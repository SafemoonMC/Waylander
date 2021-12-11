package me.eduardwayland.mooncraft.waylander.database.entities;

import org.jetbrains.annotations.NotNull;

public interface EntityChild<P extends EntityParent<P>> {

    @NotNull P getParent();
}