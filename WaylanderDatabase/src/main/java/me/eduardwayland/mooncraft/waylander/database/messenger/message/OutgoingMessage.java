package me.eduardwayland.mooncraft.waylander.database.messenger.message;

import org.jetbrains.annotations.NotNull;

public interface OutgoingMessage extends Message {

    @NotNull String asJsonString();
}