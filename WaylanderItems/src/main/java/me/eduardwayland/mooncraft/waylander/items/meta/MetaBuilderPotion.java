package me.eduardwayland.mooncraft.waylander.items.meta;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;
import me.eduardwayland.mooncraft.waylander.items.MetaBuilder;

import org.bukkit.Color;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MetaBuilderPotion extends MetaBuilder<MetaBuilderPotion,PotionMeta> {

    /*
    Constructors
     */
    protected MetaBuilderPotion(@NotNull ItemBuilder itemBuilder) {
        super(itemBuilder);
    }

    /*
    Methods
     */
    public @NotNull MetaBuilderPotion color(@NotNull Color color) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().setColor(color);
        return this;
    }

    public @NotNull MetaBuilderPotion effect(@NotNull PotionEffect potionEffect) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        return effect(potionEffect, true);
    }

    public @NotNull MetaBuilderPotion effect(@NotNull PotionEffect potionEffect, boolean overwrite) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().addCustomEffect(potionEffect, overwrite);
        return this;
    }
}