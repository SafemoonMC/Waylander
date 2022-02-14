package me.eduardwayland.mooncraft.waylander.items.meta;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;
import me.eduardwayland.mooncraft.waylander.items.MetaBuilder;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MetaBuilderFireworkEffect extends MetaBuilder<MetaBuilderFireworkEffect, FireworkEffectMeta> {


    /*
   Constructors
    */
    protected MetaBuilderFireworkEffect(@NotNull ItemBuilder itemBuilder) {
        super(itemBuilder);
    }

    /*
    Methods
     */
    public @NotNull MetaBuilderFireworkEffect effect(@NotNull FireworkEffect fireworkEffect) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().setEffect(fireworkEffect);
        return this;
    }
}