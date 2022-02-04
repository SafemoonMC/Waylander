package me.eduardwayland.mooncraft.waylander.items.records;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public record ItemEnchant(@NotNull Enchantment enchantment, int level) {

}