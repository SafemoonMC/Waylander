package me.eduardwayland.mooncraft.waylander.items.meta;

import com.mojang.authlib.GameProfile;

import me.eduardwayland.mooncraft.waylander.items.ItemBuilder;
import me.eduardwayland.mooncraft.waylander.items.MetaBuilder;
import me.eduardwayland.mooncraft.waylander.items.utility.GameProfiles;

import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Base64;
import java.util.Objects;

public class MetaBuilderHead extends MetaBuilder<SkullMeta> {

    /*
    Static
     */
    private static final Method META_SET_PROFILE_METHOD;

    static {
        try {
            META_SET_PROFILE_METHOD = SkullMeta.class.getDeclaredMethod("setProfile", GameProfile.class);
            META_SET_PROFILE_METHOD.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalStateException("No reflection can be applied for the current MetaBuilder.");
        }
    }

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
        try {
            META_SET_PROFILE_METHOD.invoke(getItemMeta(), GameProfiles.makeProfile(textureHash));
            return this;
        } catch (IllegalAccessException | InvocationTargetException e) {
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