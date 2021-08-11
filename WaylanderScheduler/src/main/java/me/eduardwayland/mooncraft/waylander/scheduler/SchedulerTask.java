package me.eduardwayland.mooncraft.waylander.scheduler;

@FunctionalInterface
public interface SchedulerTask {
    
    void cancel();
}