package me.eduardwayland.mooncraft.waylander.database.entities;

public interface EntityChild<P extends EntityParent<P>> {
    
    P getParent();
    
}