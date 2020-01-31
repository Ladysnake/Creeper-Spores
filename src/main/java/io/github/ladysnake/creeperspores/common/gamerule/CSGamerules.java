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
package io.github.ladysnake.creeperspores.common.gamerule;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.ladysnake.creeperspores.mixin.GameRulesAccessor;
import io.github.ladysnake.creeperspores.mixin.RuleTypeAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CSGamerules {
    public static final GameRules.RuleKey<EnumRule<CreeperGrief>> CREEPER_GRIEF = register("cspores_creeperGrief", EnumRule.of(CreeperGrief.CHARGED));

    public static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(String name, GameRules.RuleType<T> type) {
        return GameRulesAccessor.invokeRegister(name, type);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GameRules.Rule<T>> GameRules.RuleType<T> createRuleType(Supplier<ArgumentType<?>> argumentType, Function<GameRules.RuleType<T>, T> factory, BiConsumer<MinecraftServer, T> notifier) {
        return RuleTypeAccessor.invokeNew(argumentType, factory, notifier);
    }
}
