package me.eduardwayland.mooncraft.waylander.database.messenger;

import lombok.AccessLevel;
import lombok.Getter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import me.eduardwayland.mooncraft.waylander.database.messenger.consumer.IncomingMessageConsumer;
import me.eduardwayland.mooncraft.waylander.database.messenger.utilities.GsonProvider;
import me.eduardwayland.mooncraft.waylander.database.messenger.utilities.JsonObjectWrapper;
import me.eduardwayland.mooncraft.waylander.scheduler.AsyncScheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public final class RedisMessenger implements Messenger {

    /*
    Fields
     */
    @Getter(value = AccessLevel.PACKAGE)
    private final @NotNull String identifier;
    @Getter(value = AccessLevel.PACKAGE)
    private final @NotNull AsyncScheduler scheduler;
    @Getter(value = AccessLevel.PACKAGE)
    private final @Nullable IncomingMessageConsumer consumer;

    @Getter
    private @Nullable String incomingChannel;


    @Getter(value = AccessLevel.PACKAGE)
    private @Nullable JedisPool jedisPool;
    private @Nullable RedisSubscription subscription;

    @Getter(value = AccessLevel.PACKAGE)
    private final @NotNull ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    /*
    Constructor
     */
    public RedisMessenger(@NotNull String identifier, @NotNull AsyncScheduler scheduler, @Nullable IncomingMessageConsumer consumer) {
        this.identifier = identifier;
        this.scheduler = scheduler;
        this.consumer = consumer;
    }

    /*
    Methods
     */
    public void init(@Nullable String incomingChannel, @NotNull JedisPoolConfig jedisPoolConfig, @NotNull HostAndPort hostAndPort, @NotNull String username, @NotNull String password) {
        this.incomingChannel = incomingChannel;
        this.jedisPool = new JedisPool(jedisPoolConfig, hostAndPort.getHost(), hostAndPort.getPort(), username, password);
        if (this.incomingChannel != null) {
            this.subscription = new RedisSubscription(this);
            this.scheduler.executeAsync(this.subscription);
        }
    }

    public void stop() {
        if (this.subscription != null) this.subscription.unsubscribe();
        if (this.jedisPool != null) this.jedisPool.destroy();
    }

    public boolean isClosed() {
        return jedisPool == null || this.subscription == null || this.jedisPool.isClosed();
    }

    /*
    Override Methods
     */
    @Override
    public @NotNull CompletableFuture<Boolean> sendMessage(@NotNull String redisChannel, @NotNull String jsonOutgoingMessage) {
        if (isClosed()) return CompletableFuture.completedFuture(false);
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(redisChannel, jsonOutgoingMessage);
                return true;
            } catch (Exception e) {
                System.out.printf("[%s] [Redis Pub/Sub] Error sendMessage: %s", identifier, e.getMessage());
                return false;
            }
        }, forkJoinPool);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> addKeyValue(@NotNull String key, @NotNull String value) {
        if (isClosed()) return CompletableFuture.completedFuture(false);
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set(key, value);
                return true;
            } catch (Exception e) {
                System.out.printf("[%s] [Redis Key/Value] Error addKeyValue: %s", identifier, e.getMessage());
                return false;
            }
        }, forkJoinPool);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> delKeyValue(@NotNull String key) {
        if (isClosed()) return CompletableFuture.completedFuture(false);
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.del(key);
                return true;
            } catch (Exception e) {
                System.out.printf("[%s] [Redis Key/Value] Error delKeyValue: %s", identifier, e.getMessage());
                return false;
            }
        }, forkJoinPool);
    }

    @Override
    public @NotNull CompletableFuture<String> getKeyValue(@NotNull String key) {
        if (isClosed()) return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.get(key);
            } catch (Exception e) {
                System.out.printf("[%s] [Redis Key/Value] Error getKetValue: %s", identifier, e.getMessage());
                return null;
            }
        }, forkJoinPool);
    }

    /*
    Static Methods
     */
    public static @NotNull String encodeMessageAsJson(long timestamp, @NotNull String type, @NotNull UUID uniqueId, @Nullable JsonElement content) {
        JsonObject json = new JsonObjectWrapper()
                .add("timestamp", new JsonPrimitive(timestamp))
                .add("type", new JsonPrimitive(type))
                .add("unique-id", new JsonPrimitive(uniqueId.toString()))
                .consume(jsonObjectWrapper -> {
                    if (jsonObjectWrapper == null) return;
                    jsonObjectWrapper.add("content", content);
                })
                .toJson();
        return GsonProvider.normal().toJson(json);
    }
}