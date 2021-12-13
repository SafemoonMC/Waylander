package me.eduardwayland.mooncraft.waylander.command.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDType implements WordType<UUID> {

    /*
    Constants
     */
    private static final DynamicCommandExceptionType EXCEPTION = new DynamicCommandExceptionType(string -> new LiteralMessage("Invalid unique id: " + string));
    private static final List<String> EXAMPLES = Arrays.asList("af68e0c3-9218-49b1-a487-153e25502056", "d230edcd-9dff-3c5a-b9fa-c6eaa2a45388");

    /*
    Override Methods
     */
    @Override
    public UUID parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        try {
            return UUID.fromString(string);
        } catch (Exception e) {
            throw EXCEPTION.createWithContext(reader, string);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(S source, CommandContext<S> context, SuggestionsBuilder builder) {
        Player sender = source instanceof Player ? (Player) source : null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if ((sender == null || sender.canSee(player)) && player.getUniqueId().toString().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(player.getUniqueId().toString());
            }
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}