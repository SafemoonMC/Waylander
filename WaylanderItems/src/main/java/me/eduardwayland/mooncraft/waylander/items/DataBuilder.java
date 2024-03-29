package me.eduardwayland.mooncraft.waylander.items;

import lombok.AccessLevel;
import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.items.persistent.PersistentItemData;
import me.eduardwayland.mooncraft.waylander.items.records.ItemData;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

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
        if (!this.itemBuilder.getItemStack().hasItemMeta()) return;
        if (this.mapperClass == null) {
            throw new IllegalArgumentException("The builder doesn't contain any PersistentDataItem class reference.");
        }
        ItemMeta itemMeta = this.itemBuilder.getItemStack().getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        Constructor<?> persistentItemDataConstructor = mapperClass.getDeclaredConstructor();
        persistentItemDataConstructor.setAccessible(true);
        PersistentItemData<Q> persistentItemData = (PersistentItemData<Q>) persistentItemDataConstructor.newInstance();
        persistentItemDataConstructor.setAccessible(false);

        if (this.data == null) {
            this.data = persistentDataContainer.get(persistentItemData.getNamespacedKey(), persistentItemData.getPersistentDataType());
        } else {
            persistentDataContainer.set(persistentItemData.getNamespacedKey(), persistentItemData.getPersistentDataType(), this.data);
            this.itemBuilder.getItemStack().setItemMeta(itemMeta);
        }
    }

    /*
    Methods
     */
    public @NotNull <Q1, T1 extends PersistentItemData<Q1>> DataBuilder<Q1, T1> mapping(@NotNull Class<Q1> dataClass, @NotNull Class<T1> mapperClass) {
        DataBuilder<Q1, T1> dataBuilder = new DataBuilder<>(this.itemBuilder);
        dataBuilder.mapperClass = mapperClass;
        return dataBuilder;
    }

    public @NotNull DataBuilder<Q, T> data(@NotNull Q data) {
        this.data = data;
        return this;
    }

    public @NotNull ItemBuilder item() {
        try {
            init();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return itemBuilder;
    }

    public @NotNull ItemData<Q> data() {
        try {
            init();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new ItemData<>(this.itemBuilder.getItemStack(), this.data);
    }
}