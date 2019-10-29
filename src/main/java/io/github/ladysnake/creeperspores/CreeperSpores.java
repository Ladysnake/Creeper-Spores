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
package io.github.ladysnake.creeperspores;

import io.github.ladysnake.creeperspores.common.CreeperSporeEffect;
import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import io.github.ladysnake.creeperspores.gamerule.CSGamerules;
import io.github.ladysnake.creeperspores.gamerule.CreeperGrief;
import io.github.ladysnake.creeperspores.gamerule.EnumRule;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreeperSpores implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("creeper-spores");

    public static final EntityType<CreeperlingEntity> CREEPERLING = FabricEntityTypeBuilder
            .create(EntityCategory.MONSTER, CreeperlingEntity::new)
            .size(EntityDimensions.changing(EntityType.CREEPER.getWidth() / 2f, EntityType.CREEPER.getHeight() / 2f))
            .trackable(64, 1, true)
            .build();

    public static final StatusEffect CREEPER_SPORES_EFFECT = new CreeperSporeEffect(StatusEffectType.NEUTRAL, 0x22AA00);
    public static final Tag<Item> FERTILIZERS = TagRegistry.item(new Identifier("fabric", "fertilizers"));
    public static final GameRules.RuleKey<EnumRule<CreeperGrief>> CREEPER_GRIEF = CSGamerules.register("cspores_creeperGrief", EnumRule.of(CreeperGrief.CHARGED));

    public static final Identifier CREEPERLING_FERTILIZATION_PACKET = id("creeperling-fertilization");
    public static final String GIVE_SPORES_TAG = "cspores:giveSpores";
    public static final int MAX_SPORE_TIME = 20 * 180;

    public static Identifier id(String path) {
        return new Identifier("creeperspores", path);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.ENTITY_TYPE, CreeperSpores.id("creeperling"), CREEPERLING);
        Registry.register(Registry.STATUS_EFFECT, CreeperSpores.id("creeper_spore"), CREEPER_SPORES_EFFECT);
    }
}
