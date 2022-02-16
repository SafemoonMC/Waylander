package me.eduardwayland.mooncraft.waylander.items;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
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

public class MetaBuilder<S extends MetaBuilder<S, T>, T extends ItemMeta> {

    /*
    Constants
     */
    public static final int PAGINATION_SIZE = 30;

    /*
    Fields
     */
    final @NotNull ItemBuilder itemBuilder;
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private @NotNull T itemMeta;

    @Nullable Function<String, String> placeholderFunction;

    /*
    Constructor
     */
    @SuppressWarnings("unchecked")
    protected MetaBuilder(@NotNull ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
        this.itemMeta = (T) this.itemBuilder.getItemStack().getItemMeta();
    }

    /*
    Methods
     */
    public @NotNull S display(@NotNull String display) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        this.itemMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', display)));
        return (S) this;
    }

    public @NotNull S lore(@NotNull String description) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        return lore(description, PAGINATION_SIZE);
    }

    public @NotNull S lore(@NotNull String description, int paginationSize) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        return lore(Arrays.asList(ChatPaginator.wordWrap(ChatColor.translateAlternateColorCodes('&', description), paginationSize)));
    }

    public @NotNull S lore(@NotNull List<String> loreList) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        return lore(loreList, false);
    }

    public @NotNull S lore(@NotNull List<String> loreList, boolean append) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        List<Component> list = append && itemMeta.lore() != null ? new ArrayList<>(itemMeta.lore()) : new ArrayList<>();
        list.addAll(loreList.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).map(Component::text).map(textComponent -> (Component) textComponent).toList());
        this.itemMeta.lore(list);
        return (S) this;
    }

    public @NotNull S placeholders(@NotNull Function<String, String> placeholderFunction) {
        this.placeholderFunction = placeholderFunction;
        return (S) this;
    }

    public @NotNull S model(int model) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        this.itemMeta.setCustomModelData(model);
        return (S) this;
    }

    public @NotNull S flags(@NotNull ItemFlag... flags) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain an item.");
        this.itemMeta.addItemFlags(flags);
        return (S) this;
    }

    public @NotNull S consume(@NotNull Consumer<S> metaBuilderConsumer) {
        Objects.requireNonNull(this.itemMeta, "This builder doesn't contain a meta.");
        metaBuilderConsumer.accept((S) this);
        return (S) this;
    }

    public @NotNull ItemBuilder item() {
        if (getItemMeta().displayName() != null) {
            String display = ((TextComponent) getItemMeta().displayName()).content();
            display(placeholderFunction == null ? display : placeholderFunction.apply(display));
        }
        if (getItemMeta().lore() != null) {
            List<String> lore = new ArrayList<>();
            for (Component component : getItemMeta().lore()) {
                TextComponent textComponent = (TextComponent) component;
                String content = placeholderFunction == null ? textComponent.content() : placeholderFunction.apply(textComponent.content());
                if (content.contains("\n")) {
                    lore.addAll(Arrays.asList(content.split("\\n")));
                } else {
                    lore.add(content);
                }
            }
            lore(lore);
        }

        this.itemBuilder.getItemStack().setItemMeta(getItemMeta());
        return this.itemBuilder;
    }
}