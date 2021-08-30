package me.eduardwayland.mooncraft.waylander.command.executor.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.eduardwayland.mooncraft.waylander.command.wrapper.Brigadier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public final class Arguments {

    /*
    Constants
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();

    /*
    Initializer
     */
    static {
        PRIMITIVE_TO_WRAPPER.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(Byte.TYPE, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(Short.TYPE, Short.class);
        PRIMITIVE_TO_WRAPPER.put(Character.TYPE, Character.class);
        PRIMITIVE_TO_WRAPPER.put(Integer.TYPE, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(Long.TYPE, Long.class);
        PRIMITIVE_TO_WRAPPER.put(Float.TYPE, Float.class);
        PRIMITIVE_TO_WRAPPER.put(Double.TYPE, Double.class);
    }

    /*
    Fields
     */
    private final @NotNull Map<String, ParsedArgument<Object, ?>> argumentMap = new HashMap<>();

    /*
    Constructor
     */
    Arguments(@NotNull Map<String, ParsedArgument<Object, ?>> argumentMap) {
        this.argumentMap.putAll(argumentMap);
    }

    /*
    Static Methods
     */
    public static Arguments of(CommandContext<Object> commandContext) {
        return new Arguments(Brigadier.getArguments(commandContext));
    }

    /*
    Methods
     */
    @SuppressWarnings("unchecked")
    public <V> V getArgument(String name, Class<V> clazz) {
        ParsedArgument<Object, ?> argument = argumentMap.get(name);
        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        } else {
            Object result = (Object) argument.getResult();
            if (PRIMITIVE_TO_WRAPPER.getOrDefault(clazz, clazz).isAssignableFrom(result.getClass())) {
                return (V) result;
            } else {
                throw new IllegalArgumentException("Argument '" + name + "' is defined as " + result.getClass().getSimpleName() + ", not " + clazz);
            }
        }
    }
}