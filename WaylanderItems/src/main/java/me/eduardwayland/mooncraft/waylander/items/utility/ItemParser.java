package me.eduardwayland.mooncraft.waylander.items.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;
import me.eduardwayland.mooncraft.waylander.items.meta.MetaBuilderHead;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ItemParser {

    public static @NotNull ItemBuilder toBuilder(@NotNull ConfigurationSection configurationSection) {
        ItemBuilder itemBuilder = null;
        if (configurationSection.contains("material")) {
            Material material = Material.matchMaterial(configurationSection.getString("material"));
            if (material == null) {
                throw new IllegalArgumentException("The material " + configurationSection.getString("material") + " is not valid.");
            }
            itemBuilder = ItemBuilder.using(material);
        } else if (configurationSection.contains("head-texture")) {
            String texture = configurationSection.getString("head-texture");
            itemBuilder = ItemBuilder.using(Material.PLAYER_HEAD)
                    .meta(MetaBuilderHead.class)
                    .textureHash(texture)
                    .item();
        }

        if (itemBuilder == null) {
            throw new IllegalArgumentException("No ItemBuilder can be created from that configuration section.");
        }

        String display = configurationSection.getString("display");
        String description = configurationSection.getString("description");
        List<String> lore = configurationSection.getStringList("lore");

        return itemBuilder.meta().consume(metaBuilder -> {
            if (display != null) {
                metaBuilder.display(display);
            }
            if (description != null) {
                metaBuilder.lore(description);
                Bukkit.getLogger().info("Added description: " + description);
            }
            metaBuilder.lore(lore, true);
        }).item();
    }
}