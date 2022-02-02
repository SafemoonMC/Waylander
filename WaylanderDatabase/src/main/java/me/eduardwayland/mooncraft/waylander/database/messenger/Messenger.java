package me.eduardwayland.mooncraft.waylander.database.messenger;

import me.eduardwayland.mooncraft.waylander.database.messenger.message.OutgoingMessage;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * This interface is extended by {@link RedisMessenger} and used to restrict its methods
 */
public interface Messenger {


    /**
     * Sends the message asynchronously using {@link CompletableFuture}
     *
     * @param redisChannel    the channel to send message to
     * @param outgoingMessage the message to send
     * @return a {@link CompletableFuture<Boolean>}
     */
    @NotNull CompletableFuture<Boolean> sendMessage(@NotNull String redisChannel, @NotNull OutgoingMessage outgoingMessage);

    /**
     * Associates the value with the key in the Redis memory
     * The operation is async using {@link CompletableFuture}
     *
     * @param key   the key
     * @param value the value
     * @return a {@link CompletableFuture<Boolean>}
     */
    @NotNull CompletableFuture<Boolean> addKeyValue(@NotNull String key, @NotNull String value);

    /**
     * Removes the key from Redis memory
     * The operation is async using {@link CompletableFuture}
     *
     * @param key the key
     * @return a {@link CompletableFuture<Boolean>}
     */
    @NotNull CompletableFuture<Boolean> delKeyValue(@NotNull String key);

    /**
     * Retrieve the value assigned to the key
     * The operation is async using {@link CompletableFuture}
     *
     * @param key the key
     * @return a {@link CompletableFuture<Boolean>}
     */
    @NotNull CompletableFuture<String> getKeyValue(@NotNull String key);
}