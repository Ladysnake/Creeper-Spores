package io.github.ladysnake.creeperspores.gamerule;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.ladysnake.creeperspores.mixin.GameRulesAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CSGamerules {
    public static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(String name, GameRules.RuleType<T> type) {
        //noinspection ResultOfMethodCallIgnored
        GameRules.DO_FIRE_TICK.getClass(); // Making sure the class is loaded
        return GameRulesAccessor.invokeRegister(name, type);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GameRules.Rule<T>> GameRules.RuleType<T> createRuleType(Supplier<ArgumentType<?>> argumentType, Function<GameRules.RuleType<T>, T> factory, BiConsumer<MinecraftServer, T> notifier) {
        try {
            return RULE_TYPE_CNTR.newInstance(argumentType, factory, notifier);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new UncheckedReflectionException("Failed to instantiate RuleType", e);
        }
    }

    private static final Constructor<GameRules.RuleType> RULE_TYPE_CNTR;

    static {
        try {
            RULE_TYPE_CNTR = GameRules.RuleType.class.getDeclaredConstructor(Supplier.class, Function.class, BiConsumer.class);
            RULE_TYPE_CNTR.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new UncheckedReflectionException("Failed to reflect the constructor of GameRules.RuleType", e);
        }
    }
}
