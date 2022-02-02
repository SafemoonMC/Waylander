package me.eduardwayland.mooncraft.waylander.database.messenger.utilities;

import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;

public interface JsonElementWrapper {

    @NotNull JsonElement toJson();
}