package me.eduardwayland.mooncraft.waylander.database;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import me.eduardwayland.mooncraft.waylander.database.connection.ConnectionFactory;
import me.eduardwayland.mooncraft.waylander.database.scheme.DatabaseScheme;
import me.eduardwayland.mooncraft.waylander.database.scheme.db.NormalDatabaseScheme;
import me.eduardwayland.mooncraft.waylander.scheduler.Scheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.LongSummaryStatistics;
import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class Database {

    /*
    Fields
     */
    private final @NotNull String identifier;
    @Getter(value = AccessLevel.PACKAGE)
    private final @NotNull Scheduler scheduler;
    @Getter(value = AccessLevel.PACKAGE)
    private final @NotNull ConnectionFactory connectionFactory;
    private final @Nullable LongSummaryStatistics summaryStatistics;
    private final DatabaseManager databaseManager;

    /*
    Constructor
     */
    private Database(@NotNull String identifier, @NotNull ConnectionFactory connectionFactory, @NotNull Scheduler scheduler, @Nullable DatabaseScheme databaseScheme, @Nullable LongSummaryStatistics summaryStatistics) {
        this.identifier = identifier;
        this.scheduler = scheduler;
        this.connectionFactory = connectionFactory;
        this.summaryStatistics = summaryStatistics;

        CompletableFuture<DatabaseManager> managerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            connectionFactory.init();
            return new DatabaseManager(this);
        }, scheduler.async());

        if (!databaseScheme.getQueryList().isEmpty()) {
            managerCompletableFuture = managerCompletableFuture.thenApply(databaseManager -> {
                databaseScheme.getQueryList().stream().map(databaseManager::updateQuery).forEach(CompletableFuture::join);
                return databaseManager;
            });
        }

        databaseManager = managerCompletableFuture.join();
    }

    /*
    Methods
     */
    @SneakyThrows
    public void shutdown() {
        connectionFactory.shutdown();
        scheduler.shutdownExecutor();
        scheduler.shutdownScheduler();
    }

    /*
    Builder
     */
    public static @NotNull DatabaseBuilder builder() {
        return new DatabaseBuilder();
    }

    /*
    Inner Class
     */
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DatabaseBuilder {

        protected String identifier;
        protected DatabaseScheme databaseScheme;
        protected ConnectionFactory connectionFactory;
        protected Scheduler scheduler;
        protected LongSummaryStatistics summaryStatistics;

        public DatabaseBuilder identifier(@NotNull String identifier) {
            this.identifier = identifier.trim();
            return this;
        }

        public DatabaseBuilder databaseScheme(DatabaseScheme databaseScheme) {
            this.databaseScheme = databaseScheme;
            return this;
        }

        public DatabaseBuilder connectionFactory(@NotNull ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        public DatabaseBuilder scheduler(@NotNull Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public DatabaseBuilder statistics() {
            this.summaryStatistics = new LongSummaryStatistics();
            return this;
        }

        public Database build() {
            if (identifier == null || identifier.isEmpty()) {
                throw new IllegalArgumentException("The identifier cannot be null or empty.");
            }
            if (connectionFactory == null) {
                throw new IllegalArgumentException("The connection factory cannot be null.");
            }
            if (scheduler == null) {
                throw new IllegalArgumentException("The scheduler cannot be null.");
            }
            if (databaseScheme == null) {
                databaseScheme = new NormalDatabaseScheme("Undefined", new LinkedList<>());
            }
            return new Database(identifier, connectionFactory, scheduler, databaseScheme, summaryStatistics);
        }
    }
}