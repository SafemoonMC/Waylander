package me.eduardwayland.mooncraft.waylander.command.suggest;

import com.mojang.brigadier.Message;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class Suggestion {

    /*
    Fields
     */
    private final @NotNull String argument;
    private final @Nullable Message tooltip;

}