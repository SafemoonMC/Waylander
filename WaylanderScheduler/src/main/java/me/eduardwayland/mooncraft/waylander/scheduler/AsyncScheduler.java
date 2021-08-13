package me.eduardwayland.mooncraft.waylander.scheduler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.*;

public abstract class AsyncScheduler implements Scheduler {

    /*
    Fields
     */
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final ErrorReportingExecutor errorReportingExecutor;
    private final ForkJoinPool forkJoinPool;

    /*
    Constructor
     */
    public AsyncScheduler(String identifier) {
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1,
                ThreadFactoryImpl.builder()
                        .setDaemon(true)
                        .setNameFormat(identifier + "-scheduler")
                        .build()
                        .create()
        );
        this.scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        this.errorReportingExecutor = new ErrorReportingExecutor(Executors.newCachedThreadPool(
                ThreadFactoryImpl.builder()
                        .setDaemon(true)
                        .setNameFormat(identifier + "-scheduler-worker-%d")
                        .build()
                        .create()
        ));
        this.forkJoinPool = new ForkJoinPool(32, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> e.printStackTrace(), false);
    }

    @Override
    public Executor async() {
        return forkJoinPool;
    }

    @Override
    public SchedulerTask asyncLater(Runnable runnable, long delay, TimeUnit timeUnit) {
        ScheduledFuture<?> future = this.scheduledThreadPoolExecutor.schedule(() -> this.errorReportingExecutor.execute(runnable), delay, timeUnit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable runnable, long interval, TimeUnit timeUnit) {
        ScheduledFuture<?> future = this.scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> this.errorReportingExecutor.execute(runnable), 0, interval, timeUnit);
        return () -> future.cancel(false);
    }

    @Override
    public void shutdownExecutor() {
        this.errorReportingExecutor.executorService.shutdown();
        try {
            this.errorReportingExecutor.executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownScheduler() {
        this.scheduledThreadPoolExecutor.shutdown();
        try {
            this.scheduledThreadPoolExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Inner Classes
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ErrorReportingExecutor implements Executor {

        private final ExecutorService executorService;

        @Override
        public void execute(@NonNull Runnable command) {
            this.executorService.execute(new ErrorReportingRunnable(command));
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ErrorReportingRunnable implements Runnable {

        private final Runnable runnable;

        @Override
        public void run() {
            try {
                this.runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}