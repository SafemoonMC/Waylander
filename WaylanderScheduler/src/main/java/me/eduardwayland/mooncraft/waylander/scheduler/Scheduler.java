package me.eduardwayland.mooncraft.waylander.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public interface Scheduler {

    Executor sync();

    Executor async();

    default void executeAsync(Runnable runnable) {
        async().execute(runnable);
    }

    default void executeSync(Runnable runnable) {
        sync().execute(runnable);
    }

    SchedulerTask asyncLater(Runnable runnable, long delay, TimeUnit timeUnit);

    SchedulerTask asyncRepeating(Runnable runnable, long interval, TimeUnit timeUnit);

    void shutdownExecutor();

    void shutdownScheduler();
}