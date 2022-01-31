package me.eduardwayland.mooncraft.waylander.items.meta;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;
import me.eduardwayland.mooncraft.waylander.items.MetaBuilder;

import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MetaBuilderArmor extends MetaBuilder<LeatherArmorMeta> {

    /*
    Constructors
     */
    protected MetaBuilderArmor(@NotNull ItemBuilder itemBuilder) {
        super(itemBuilder);
    }

    /*
    Methods
     */
    public @NotNull MetaBuilderArmor color(@NotNull Color color) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().setColor(color);
        return this;
    }
}