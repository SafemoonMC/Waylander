package me.eduardwayland.mooncraft.waylander.items;

import lombok.AccessLevel;
import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.items.records.ItemEnchant;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class ItemBuilder {

    /*
    Fields
     */
    @Getter(value = AccessLevel.PACKAGE)
    private final @NotNull ItemStack itemStack;

    /*
    Constructors
     */
    protected ItemBuilder(@NotNull Material material) {
        this.itemStack = new ItemStack(material);
    }

    protected ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /*
    Static Methods
     */
    public static @NotNull ItemBuilder using(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    public static @NotNull ItemBuilder using(@NotNull ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    /*
    Methods
     */
    public @NotNull ItemBuilder type(@NotNull Material material) {
        Objects.requireNonNull(this.itemStack, "This builder doesn't contain an item.");
        this.itemStack.setType(material);
        return this;
    }

    public @NotNull ItemBuilder amount(int amount) {
        Objects.requireNonNull(this.itemStack, "This builder doesn't contain an item.");
        this.itemStack.setAmount(amount);
        return this;
    }

    public @NotNull ItemBuilder enchant(@NotNull ItemEnchant itemEnchant) {
        Objects.requireNonNull(this.itemStack, "This builder doesn't contain an item.");
        if (itemEnchant.level() <= 0) {
            this.itemStack.removeEnchantment(itemEnchant.enchantment());
        } else {
            this.itemStack.addUnsafeEnchantment(itemEnchant.enchantment(), itemEnchant.level());
        }
        return this;
    }

    public @NotNull ItemBuilder consume(@NotNull Consumer<ItemBuilder> itemBuilderConsumer) {
        itemBuilderConsumer.accept(this);
        return this;
    }

    @SuppressWarnings("unchecked")
    public @NotNull MetaBuilder<?, ItemMeta> meta() {
        return meta(MetaBuilder.class);
    }

    public @NotNull <T extends MetaBuilder<T, ? extends ItemMeta>> T meta(@NotNull Class<T> metaBuilderClass) {
        try {
            return metaBuilderClass.getDeclaredConstructor(ItemBuilder.class).newInstance(this);
        } catch (Exception e) {
            throw new IllegalArgumentException("That MetaBuilder doesn't have the required constructor!", e);
        }
    }

    public @NotNull DataBuilder<?, ?> data() {
        return new DataBuilder<>(this);
    }

    public @NotNull ItemStack stack() {
        return this.itemStack;
    }
}