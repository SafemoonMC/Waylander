package me.eduardwayland.mooncraft.waylander.command.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

@FunctionalInterface
public interface WordType<T> extends Type<T> {

    /*
    Constants
     */
    public static final StringArgumentType WORD = StringArgumentType.word();

    /*
    Override Methods
     */
    @Override
    default ArgumentType<?> mapped() {
        return WORD;
    }
}