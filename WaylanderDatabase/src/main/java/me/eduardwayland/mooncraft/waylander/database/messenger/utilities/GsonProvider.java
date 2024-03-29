package me.eduardwayland.mooncraft.waylander.database.messenger.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class GsonProvider {

    /*
    Constants
     */
    private static final @NotNull Gson NORMAL = new GsonBuilder().generateNonExecutableJson().disableHtmlEscaping().create();
    private static final @NotNull Gson PRETTY_PRINTING = new GsonBuilder().generateNonExecutableJson().disableHtmlEscaping().setPrettyPrinting().create();

    /*
    Static Methods
     */
    public static @NotNull Gson normal() {
        return NORMAL;
    }

    public static @NotNull Gson prettyPrinting() {
        return PRETTY_PRINTING;
    }
}