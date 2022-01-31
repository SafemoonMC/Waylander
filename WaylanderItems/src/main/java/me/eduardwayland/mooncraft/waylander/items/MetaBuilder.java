package me.eduardwayland.mooncraft.waylander.items;

import lombok.AccessLevel;
import lombok.Getter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class MetaBuilder<T extends ItemMeta> {

    /*
    Constants
     */
    public static final int PAGINATION_SIZE = 40;

    /*
    Fields
     */
    final @NotNull ItemBuilder itemBuilder;
    @Getter(value = AccessLevel.PROTECTED)
    final @NotNull T itemMeta;

    @Nullable Function<String, String> placeholderFunction;

    /*
    Constructor
     */
    @SuppressWarnings("unchecked")
    protected MetaBuilder(@NotNull ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
        this.itemMeta = (T) this.itemBuilder.itemStack.getItemMeta();
    }

    /*
    Methods
     */
    public @NotNull MetaBuilder<T> display(@NotNull String display) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        this.itemMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', display)));
        return this;
    }

    public @NotNull MetaBuilder<T> lore(@NotNull String description) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        return lore(description, PAGINATION_SIZE);
    }

    public @NotNull MetaBuilder<T> lore(@NotNull String description, int paginationSize) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        return lore(Arrays.asList(ChatPaginator.wordWrap(ChatColor.translateAlternateColorCodes('&', description), paginationSize)));
    }

    public @NotNull MetaBuilder<T> lore(@NotNull List<String> loreList) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        return lore(loreList, false);
    }

    public @NotNull MetaBuilder<T> lore(@NotNull List<String> loreList, boolean append) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        List<Component> list = append && itemMeta.lore() != null ? new ArrayList<>(itemMeta.lore()) : new ArrayList<>();
        list.addAll(loreList.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).map(Component::text).map(textComponent -> (Component) textComponent).toList());
        this.itemMeta.lore(list);
        return this;
    }

    public @NotNull MetaBuilder<T> placeholder(@NotNull Function<String, String> placeholderFunction) {
        this.placeholderFunction = placeholderFunction;
        return this;
    }

    public @NotNull MetaBuilder<T> model(int model) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        this.itemMeta.setCustomModelData(model);
        return this;
    }

    public @NotNull MetaBuilder<T> consume(@NotNull Consumer<MetaBuilder<T>> metaBuilderConsumer) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        metaBuilderConsumer.accept(this);
        return this;
    }

    public @NotNull ItemBuilder item() {
        if (placeholderFunction != null) {
            if (getItemMeta().displayName() != null) {
                String display = ((TextComponent) getItemMeta().displayName()).content();
                display(ChatColor.translateAlternateColorCodes('&', placeholderFunction.apply(display)));
            }
        }
        if (getItemMeta().lore() != null) {
            List<String> lore = new ArrayList<>();
            for (Component component : getItemMeta().lore()) {
                TextComponent textComponent = (TextComponent) component;
                if (textComponent.content().contains("\n")) {
                    lore.addAll(Arrays.asList(textComponent.content().split("\n")));
                } else {
                    lore.add(textComponent.content());
                }
            }
            getItemMeta().lore(lore.stream().map(line -> placeholderFunction != null ? ChatColor.translateAlternateColorCodes('&', placeholderFunction.apply(line)) : line).map(line -> (Component) Component.text(line)).toList());
        }

        this.itemBuilder.itemStack.setItemMeta(getItemMeta());
        return this.itemBuilder;
    }
}