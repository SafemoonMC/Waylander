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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerType implements WordType<Player> {

    /*
    Constants
     */
    private static final DynamicCommandExceptionType EXCEPTION = new DynamicCommandExceptionType(name -> new LiteralMessage("Unknown player: " + name));
    private static final List<String> EXAMPLES = Arrays.asList("Eduard", "Wayland");

    /*
    Override Methods
     */
    @Override
    public Player parse(StringReader reader) throws CommandSyntaxException {
        String username = reader.readUnquotedString();
        Player player = Bukkit.getPlayerExact(username);
        if (player == null) throw EXCEPTION.createWithContext(reader, username);

        return player;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(S source, CommandContext<S> context, SuggestionsBuilder builder) {
        Player sender = source instanceof Player ? (Player) source : null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if ((sender == null || sender.canSee(player)) && player.getName().startsWith(builder.getRemaining()))
                builder.suggest(player.getName());
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}