package me.eduardwayland.mooncraft.waylander.scheduler;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public interface Scheduler {

    @NotNull Executor sync();

    @NotNull Executor async();

    default void executeAsync(@NotNull Runnable runnable) {
        async().execute(runnable);
    }

    default void executeSync(@NotNull Runnable runnable) {
        sync().execute(runnable);
    }

    @NotNull SchedulerTask syncLater(@NotNull Runnable runnable, long delay);

    default @NotNull SchedulerTask syncRepeating(@NotNull Runnable runnable, long interval) {
        return syncRepeating(runnable, 0, interval);
    }

    @NotNull SchedulerTask syncRepeating(@NotNull Runnable runnable, long delay, long interval);

    @NotNull SchedulerTask asyncLater(@NotNull Runnable runnable, long delay, @NotNull TimeUnit timeUnit);

    default @NotNull SchedulerTask asyncRepeating(@NotNull Runnable runnable, long interval, @NotNull TimeUnit timeUnit) {
        return asyncRepeating(runnable, 0, interval, timeUnit);
    }

    @NotNull SchedulerTask asyncRepeating(@NotNull Runnable runnable, long delay, long interval, @NotNull TimeUnit timeUnit);

    void shutdownExecutor();

    void shutdownScheduler();
}