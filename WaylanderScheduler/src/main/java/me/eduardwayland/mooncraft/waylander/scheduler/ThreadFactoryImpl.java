package me.eduardwayland.mooncraft.waylander.scheduler;

import lombok.Builder;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

@Builder(setterPrefix = "set")
class ThreadFactoryImpl {


    private String nameFormat;
    @Builder.Default
    private Boolean daemon = null;
    @Builder.Default
    private int priority = Thread.NORM_PRIORITY;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private ThreadFactory backingThreadFactory;

    public ThreadFactory create() {
        final ThreadFactory backingThreadFactory = (this.backingThreadFactory != null) ? this.backingThreadFactory : Executors.defaultThreadFactory();
        final AtomicLong count = (nameFormat != null) ? new AtomicLong(0) : null;

        return runnable -> {
            Thread thread = backingThreadFactory.newThread(runnable);
            if (nameFormat != null) {
                thread.setName(String.format(Locale.ROOT, nameFormat, count.getAndIncrement()));
            }
            if (daemon != null)
                thread.setDaemon(daemon);
            thread.setPriority(priority);
            if (uncaughtExceptionHandler != null) {
                thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            }
            return thread;
        };
    }
}
