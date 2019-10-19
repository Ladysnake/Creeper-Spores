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

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.CommandContext;
import io.github.ladysnake.creeperspores.CreeperSpores;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;

public final class EnumRule<E extends Enum<E>> extends GameRules.Rule<EnumRule<E>> {
    private final Class<E> enumType;
    private E value;

    public static <E extends Enum<E>> GameRules.RuleType<EnumRule<E>> of(Class<E> enumType, E value, BiConsumer<MinecraftServer, EnumRule<E>> notifier) {
        Preconditions.checkArgument(enumType.isInstance(value));
        Preconditions.checkArgument(enumType.isEnum());
        Preconditions.checkArgument(enumType.getEnumConstants().length > 0);
        return CSGamerules.createRuleType(() -> new EnumArgumentType<>(enumType), (type) -> new EnumRule<>(type, enumType, value), notifier);
    }

    public static <E extends Enum<E>> GameRules.RuleType<EnumRule<E>> of(E initialValue) {
        return of(initialValue.getDeclaringClass(), initialValue, (server, rule) -> {});
    }

    private EnumRule(GameRules.RuleType<EnumRule<E>> ruleType, Class<E> enumType, E value) {
        super(ruleType);
        this.enumType = enumType;
        this.value = value;
    }

    protected void setFromArgument(CommandContext<ServerCommandSource> commandContext, String name) {
        this.value = commandContext.getArgument(name, this.enumType);
    }

    public E get() {
        return this.value;
    }

    protected String valueToString() {
        return this.value.toString();
    }

    protected void setFromString(String value) {
        if (!value.isEmpty()) {
            try {
                this.value = Enum.valueOf(this.enumType, value);
            } catch (IllegalArgumentException e) {
                CreeperSpores.LOGGER.warn("[Creeper Spores] Failed to parse enum {} for {}", value, this.enumType.getName());
                this.value = this.enumType.getEnumConstants()[0];
            }
        }
    }

    public int toCommandResult() {
        return this.value.ordinal();
    }

    @Override
    protected EnumRule<E> getThis() {
        return this;
    }
}
