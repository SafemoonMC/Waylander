package me.eduardwayland.mooncraft.waylander.items.persistent;

import lombok.Getter;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class PersistentItemData<T> {

    /*
    Fields
     */
    private final @NotNull Plugin plugin;
    @Getter
    private final @NotNull NamespacedKey namespacedKey;
    @Getter
    private final @NotNull PersistentDataType<PersistentDataContainer, T> persistentDataType;

    /*
    Constructor
     */
    public PersistentItemData(@NotNull Plugin plugin, @NotNull NamespacedKey namespacedKey) {
        this.plugin = plugin;
        this.namespacedKey = namespacedKey;
        this.persistentDataType = new PersistentDataType<>() {
            @Override
            public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
                return PersistentDataContainer.class;
            }

            @Override
            @SuppressWarnings("unchecked")
            public @NotNull Class<T> getComplexType() {
                return (Class<T>) this.getClass();
            }

            @Override
            public @NotNull PersistentDataContainer toPrimitive(@NotNull T t, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
                PersistentDataContainer persistentDataContainer = persistentDataAdapterContext.newPersistentDataContainer();
                return toContainer(t, persistentDataContainer);
            }

            @Override
            public @NotNull T fromPrimitive(@NotNull PersistentDataContainer persistentDataContainer, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
                return fromContainer(persistentDataContainer);
            }
        };
    }

    /*
    Abstract Methods
     */
    public abstract @NotNull PersistentDataContainer toContainer(@NotNull T value, @NotNull PersistentDataContainer persistentDataContainer);

    public abstract @NotNull T fromContainer(@NotNull PersistentDataContainer persistentDataContainer);

    /*
    Methods
     */
    protected @NotNull NamespacedKey key(@NotNull String key) {
        return new NamespacedKey(plugin, key);
    }
}