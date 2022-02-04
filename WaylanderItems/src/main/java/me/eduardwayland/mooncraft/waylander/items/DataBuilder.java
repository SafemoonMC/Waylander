package me.eduardwayland.mooncraft.waylander.items;

import lombok.AccessLevel;
import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.items.persistent.PersistentItemData;
import me.eduardwayland.mooncraft.waylander.items.records.ItemData;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataBuilder<Q, T extends PersistentItemData<Q>> {

    /*
    Fields
     */
    final @NotNull ItemBuilder itemBuilder;

    @Getter(value = AccessLevel.PACKAGE)
    private @Nullable Class<?> mapperClass;
    @Getter(value = AccessLevel.PACKAGE)
    private @Nullable Q data;



    /*
    Constructor
     */
    protected DataBuilder(@NotNull ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }

    @SuppressWarnings("unchecked")
    private void init() throws Exception {
        if (this.mapperClass == null) {
            throw new IllegalArgumentException("The builder doesn't contain any PersistentDataItem class reference.");
        }
        ItemMeta itemMeta = this.itemBuilder.getItemStack().getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        PersistentItemData<Q> persistentItemData = (PersistentItemData<Q>) mapperClass.getDeclaredConstructor().newInstance();

        if (this.data == null) {
            this.data = persistentDataContainer.get(persistentItemData.getNamespacedKey(), persistentItemData.getPersistentDataType());
        } else {
            persistentDataContainer.set(persistentItemData.getNamespacedKey(), persistentItemData.getPersistentDataType(), this.data);
        }
        this.itemBuilder.getItemStack().setItemMeta(itemMeta);
    }

    /*
    Methods
     */
    public @NotNull <Q1, T1 extends PersistentItemData<Q1>> DataBuilder<Q1, T1> mapping(@NotNull Class<Q1> dataClass, @NotNull Class<T1> mapperClass) {
        this.mapperClass = mapperClass;
        return new DataBuilder<>(this.itemBuilder);
    }

    public @NotNull DataBuilder<Q, T> data(@NotNull Q data) {
        this.data = data;
        return this;
    }

    public @NotNull ItemBuilder item() throws Exception {
        try {
            init();
        } catch (Exception ignored) {
        }
        return itemBuilder;
    }

    public @NotNull ItemData<Q> data() {
        try {
            init();
        } catch (Exception ignored) {
        }
        return new ItemData<>(this.itemBuilder.getItemStack(), this.data);
    }
}