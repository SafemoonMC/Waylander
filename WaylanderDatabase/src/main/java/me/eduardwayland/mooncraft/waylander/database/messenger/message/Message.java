package me.eduardwayland.mooncraft.waylander.database.messenger.message;

import me.eduardwayland.mooncraft.waylander.database.messenger.RedisMessenger;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a message sent received via {@link RedisMessenger}.
 */
@ApiStatus.NonExtendable
public interface Message {

    /**
     * Gets the unique id associated with this message.
     */
    @NotNull UUID getUniqueId();

    /**
     * Gets the time when the message has been created.
     */
    @NotNull Instant getTimestamp();
}