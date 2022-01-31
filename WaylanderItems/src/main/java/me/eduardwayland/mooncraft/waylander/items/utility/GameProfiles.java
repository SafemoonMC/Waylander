package me.eduardwayland.mooncraft.waylander.items.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class GameProfiles {

    public static @NotNull GameProfile makeProfile(@NotNull String hash) {
        UUID uniqueId = new UUID(hash.substring(hash.length() - 20).hashCode(), hash.substring(hash.length() - 10).hashCode());
        GameProfile gameProfile = new GameProfile(uniqueId, "WaylanderItems");
        gameProfile.getProperties().put("textures", new Property("textures", hash));
        return gameProfile;
    }
}