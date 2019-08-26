/*
 * Creeper-Spores
 * Copyright (C) 2019 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
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
