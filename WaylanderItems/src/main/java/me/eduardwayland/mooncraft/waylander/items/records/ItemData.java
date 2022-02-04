package me.eduardwayland.mooncraft.waylander.items.records;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ItemData<T>(@NotNull ItemStack itemStack, @Nullable T data) {

    public boolean hasData() {
        return this.data != null;
    }
}