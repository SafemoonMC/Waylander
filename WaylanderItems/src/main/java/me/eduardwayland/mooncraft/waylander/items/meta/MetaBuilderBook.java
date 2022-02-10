package me.eduardwayland.mooncraft.waylander.items.meta;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;
import me.eduardwayland.mooncraft.waylander.items.MetaBuilder;

import net.kyori.adventure.text.Component;

import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MetaBuilderBook extends MetaBuilder<MetaBuilderBook, BookMeta> {

    /*
    Constructors
     */
    protected MetaBuilderBook(@NotNull ItemBuilder itemBuilder) {
        super(itemBuilder);
    }

    /*
    Methods
     */
    public @NotNull MetaBuilderBook title(@NotNull String title) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().setTitle(ChatColor.translateAlternateColorCodes('&', title));
        return this;
    }

    public @NotNull MetaBuilderBook author(@NotNull String author) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().setAuthor(ChatColor.translateAlternateColorCodes('&', author));
        return this;
    }

    public @NotNull MetaBuilderBook generation(@NotNull BookMeta.Generation generation) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().setGeneration(generation);
        return this;
    }

    public @NotNull MetaBuilderBook page(@NotNull String page) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().addPages(Component.text(ChatColor.translateAlternateColorCodes('&', page)));
        return this;
    }
}