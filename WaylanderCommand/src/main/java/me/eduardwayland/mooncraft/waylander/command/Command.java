package me.eduardwayland.mooncraft.waylander.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.command.arguments.Type;
import me.eduardwayland.mooncraft.waylander.command.executor.arguments.Arguments;
import me.eduardwayland.mooncraft.waylander.command.suggest.Suggestion;
import me.eduardwayland.mooncraft.waylander.command.wrapper.Brigadier;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@AllArgsConstructor
public abstract class Command<S extends CommandSender> {

    /*
    Fields
     */
    private final @NotNull String name;
    private final @NotNull String description;
    private final @Nullable Permission permission;

    private final @NotNull List<Command<S>> children = new ArrayList<>();
    private final @NotNull List<LiteralCommand<S>> literals = new ArrayList<>();
    private final @NotNull List<RequiredCommand<S, ?>> arguments = new ArrayList<>();
    private final @NotNull Map<Integer, List<BaseComponent>> helpComponentList = new HashMap<>();

    /*
    Methods
     */
    public boolean hasPermission(@NotNull CommandSender commandSender) {
        return permission == null || commandSender.hasPermission(permission);
    }

    @NotNull
    public LiteralArgumentBuilder<Object> toLiteralArgumentBuilder() {
        return toLiteralArgumentBuilder(true);
    }

    @NotNull
    public LiteralArgumentBuilder<Object> toLiteralArgumentBuilder(boolean createHelpCommand) {
        LiteralArgumentBuilder<Object> argumentBuilder = LiteralArgumentBuilder.literal(getName());
        append(argumentBuilder, this);
        if (createHelpCommand) {
            argumentBuilder
                    .then(LiteralArgumentBuilder.literal("help")
                            .executes(commandContext -> {
                                CommandSender commandSender = Brigadier.getBukkitSender(commandContext.getSource());
                                sendHelpMessage(commandSender, 1);
                                return 1;
                            })
                            .then(RequiredArgumentBuilder.argument("page", IntegerArgumentType.integer(1, getHelpPageCount()))
                                    .executes(commandContext -> {
                                        CommandSender commandSender = Brigadier.getBukkitSender(commandContext.getSource());
                                        sendHelpMessage(commandSender, commandContext.getArgument("page", Integer.class));
                                        return 1;
                                    })
                            ));
        }

        return argumentBuilder;
    }

    /*
    Private Methods
     */
    @NotNull
    @SuppressWarnings("unchecked")
    private ArgumentBuilder<Object, ?> append(@NotNull ArgumentBuilder<Object, ?> node, @NotNull Command<S> command) {
        for (LiteralCommand<S> literalCommand : command.getLiterals()) {
            ArgumentBuilder<Object, ?> literalArgumentBuilder = append(LiteralArgumentBuilder.literal(literalCommand.getName()), literalCommand)
                    .executes(commandContext -> wrapLiteralExecute(literalCommand, commandContext));
            node.then(literalArgumentBuilder);
            Arrays.stream(literalCommand.getAliases()).forEach(alias -> node.then(append(LiteralArgumentBuilder.literal(alias), literalCommand)).executes(object -> wrapLiteralExecute(literalCommand, object)));
        }
        for (RequiredCommand<S, ?> requiredCommand : command.getArguments()) {
            Type<?> type = null;
            ArgumentType<?> finalArgumentType;
            ArgumentType<?> argumentType = requiredCommand.getArgumentType();
            if (argumentType instanceof Type) {
                type = (Type<?>) argumentType;
                finalArgumentType = type.mapped();
            } else finalArgumentType = argumentType;

            Type<?> finalType = type;
            if (requiredCommand.getSuggestions() != null && !requiredCommand.getSuggestions().isEmpty()) {
                node.then(append(RequiredArgumentBuilder.argument(requiredCommand.getName(), finalArgumentType)
                        .suggests(((context, builder) -> {
                            for (Suggestion suggestion : requiredCommand.getSuggestions().getSuggestionList()) {
                                if (suggestion.getTooltip() == null) builder.suggest(suggestion.getArgument());
                                else builder.suggest(suggestion.getArgument(), suggestion.getTooltip());
                            }
                            if (finalType != null) return finalType.listSuggestions(context, builder);
                            return builder.buildFuture();
                        }))
                        .executes(commandContext -> wrapRequiredExecute(requiredCommand, commandContext)), requiredCommand));
            } else
                node.then(append(RequiredArgumentBuilder
                        .argument(requiredCommand.getName(), finalArgumentType)
                        .executes(commandContext -> wrapRequiredExecute(requiredCommand, commandContext))
                        .suggests(((commandContext, suggestionsBuilder) -> finalType == null ? Suggestions.empty() : finalType.listSuggestions(commandContext, suggestionsBuilder))), requiredCommand));
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    protected int wrapLiteralExecute(@NotNull LiteralCommand<S> literalCommand, @NotNull CommandContext<Object> commandContext) {
        CommandSender commandSender = Brigadier.getBukkitSender(commandContext.getSource());
        if (literalCommand.getExecutor() == null) return 0;
        if (!literalCommand.hasPermission(commandSender)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4No permission!"));
            return 0;
        }
        try {
            literalCommand.getExecutor().onExecute((S) commandSender);
            return 1;
        } catch (ClassCastException e) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You cannot execute this command here!"));
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    protected int wrapRequiredExecute(@NotNull RequiredCommand<S, ?> requiredCommand, @NotNull CommandContext<Object> commandContext) throws CommandSyntaxException {
        CommandSender commandSender = Brigadier.getBukkitSender(commandContext.getSource());
        if (requiredCommand.getExecutor() == null) return 0;
        if (!requiredCommand.hasPermission(commandSender)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4No permission!"));
            return 0;
        }
        try {
            requiredCommand.getExecutor().onExecute((S) commandSender, Arguments.of(commandContext));
            return 1;
        } catch (ClassCastException e) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You cannot execute this command here!"));
            return 0;
        }
    }

    protected void sendHelpMessage(@NotNull CommandSender commandSender, int page) {
        int index = page - 1;
        int minimum = index * 5;

        List<BaseComponent[]> baseComponents = new ArrayList<>();
        baseComponents.add(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', String.format("&6&m&l     &r&e Help &7(Page %d/%d) &6&m&l     ", page, getHelpPageCount()))).create());

        children.stream().sorted(Comparator.comparingInt(o -> o.getChildren().size())).skip(minimum).limit(5).forEach(command -> {
            BaseComponent[] components = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', String.format(" &8/&f%s %s", getName(), command.getName()))).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(command.getDescription()))).create();
            baseComponents.add(components);
        });
        baseComponents.forEach(components -> commandSender.spigot().sendMessage(components));
    }

    protected int getHelpPageCount() {
        if (children.size() == 0) return 1;
        if (children.size() < 5) return 1;
        return children.size() / 5;
    }
}