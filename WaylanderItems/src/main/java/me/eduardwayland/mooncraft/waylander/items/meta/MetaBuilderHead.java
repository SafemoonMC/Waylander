package me.eduardwayland.mooncraft.waylander.items.meta;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;
import me.eduardwayland.mooncraft.waylander.items.MetaBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public class MetaBuilderHead extends MetaBuilder<MetaBuilderHead, SkullMeta> {

    /*
    Constructors
     */
    protected MetaBuilderHead(@NotNull ItemBuilder itemBuilder) {
        super(itemBuilder);
    }

    /*
    Methods
     */
    public @NotNull MetaBuilderHead player(@NotNull Player player) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");
        getItemMeta().setOwningPlayer(player);
        return this;
    }

    public @NotNull MetaBuilderHead textureHash(@NotNull String textureHash) {
        Objects.requireNonNull(getItemMeta(), "This builder doesn't contain a meta.");

        UUID hashAsId = new UUID(textureHash.hashCode(), textureHash.hashCode());
        try {
            ItemStack newItemStack = Bukkit.getUnsafe().modifyItemStack(this.item().getItemStack(), "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + textureHash + "\"}]}}}");
            setItemMeta((SkullMeta) newItemStack.getItemMeta());
            return this;
        } catch (Exception e) {
            throw new IllegalStateException("No reflection can be applied for the current MetaBuilder.");
        }
    }

    public @NotNull MetaBuilderHead textureLink(@NotNull String textureLink) {
        URI uri;
        try {
            uri = new URI(textureLink);
        } catch (Exception e) {
            throw new IllegalArgumentException("The texture link syntax is wrong.");
        }
        String encoding = "{\"textures\":{\"SKIN\":{\"url\":\"" + uri.toString() + "\"}}}";
        return textureHash(Base64.getEncoder().encodeToString(encoding.getBytes()));
    }
}