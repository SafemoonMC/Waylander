package me.eduardwayland.mooncraft.waylander.database.messenger.consumer;

import me.eduardwayland.mooncraft.waylander.database.messenger.RedisSubscription;

import org.jetbrains.annotations.NotNull;


/**
 * A simply functional interface called by {@link RedisSubscription} with the received message
 */
@FunctionalInterface
public interface IncomingMessageConsumer {

    /**
     * @param jsonMessage the json message to consume
     * @return true if the message has been consumed else returns false
     */
    boolean consumeIncomingMessageAsString(@NotNull String jsonMessage);
}