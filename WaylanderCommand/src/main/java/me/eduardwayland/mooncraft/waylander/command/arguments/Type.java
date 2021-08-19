package me.eduardwayland.mooncraft.waylander.command.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public interface Type<T> extends ArgumentType<T> {

    @Override
    default <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return listSuggestions(context.getSource(), context, builder);
    }

    default <S> CompletableFuture<Suggestions> listSuggestions(S source, CommandContext<S> context, SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    ArgumentType<?> mapped();
}