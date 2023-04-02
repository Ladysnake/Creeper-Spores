/*
 * Creeper Spores
 * Copyright (C) 2019-2023 Ladysnake
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

import com.google.common.base.Suppliers;
import io.github.ladysnake.creeperspores.common.CreeperSporeEffect;
import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import io.github.ladysnake.creeperspores.mixin.EntityTypeAccessor;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CreeperSpores implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("creeper-spores");

    /** Identifiers corresponding to entity types that should be {@linkplain #registerCreeperLike(Identifier, EntityType)
        registered as creeper likes} if and when the entity type gets registered to {@link Registries#ENTITY_TYPE}.*/
    public static final Set<Identifier> CREEPER_LIKES = new HashSet<>(Arrays.asList(
            new Identifier("minecraft", "creeper"),
            new Identifier("mobz", "creep_entity"),
            new Identifier("mobz", "crip_entity")
    ));

    public static final TagKey<Item> FERTILIZERS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "fertilizers"));
    public static final TagKey<DamageType> SPAWNS_MORE_CREEPERLINGS = TagKey.of(RegistryKeys.DAMAGE_TYPE, id("spawns_more_creeperlings"));
    public static final TagKey<DamageType> EXTRA_CREEPER_DAMAGE = TagKey.of(RegistryKeys.DAMAGE_TYPE, id("extra_creeper_damage"));

    public static final Identifier CREEPERLING_FERTILIZATION_PACKET = id("creeperling-fertilization");
    public static final String GIVE_SPORES_TAG = "cspores:giveSpores";
    public static final int MAX_SPORE_TIME = 20 * 180;

    public static final GameRules.Key<EnumRule<CreeperGrief>> CREEPER_GRIEF = registerGamerule(
            "creeper-spores:creeperGrief",
            GameRuleFactory.createEnumRule(CreeperGrief.CHARGED)
    );
    public static final GameRules.Key<DoubleRule> CREEPER_REPLACE_CHANCE = registerGamerule(
            "creeper-spores:creeperReplaceChance",
            GameRuleFactory.createDoubleRule(0.2, 0, 1)
    );

    public static Identifier id(String path) {
        return new Identifier("creeperspores", path);
    }

    public static <T> void visitRegistry(Registry<T> registry, BiConsumer<Identifier, T> visitor) {
        RegistryEntryAddedCallback.event(registry).register((index, identifier, entry) -> visitor.accept(identifier, entry));
        new HashSet<>(registry.getIds()).forEach(id -> visitor.accept(id, registry.get(id)));
    }

    @Override
    public void onInitialize(ModContainer mod) {
        visitRegistry(Registries.ENTITY_TYPE, (id, type) -> {
            if (CREEPER_LIKES.contains(id)) {
                // can't actually check that the entity type is living, so just hope nothing goes wrong
                @SuppressWarnings("unchecked") EntityType<? extends LivingEntity> livingType = (EntityType<? extends LivingEntity>) type;
                registerCreeperLike(id, livingType);
            }
        });
    }

    private static <T extends GameRules.Rule<T>> GameRules.Key<T> registerGamerule(String name, GameRules.Type<T> type) {
        return GameRuleRegistry.register(name, GameRules.Category.MOBS, type);
    }

    @ApiStatus.Internal
    public static void registerCreeperLike(Identifier id) {
        // can't actually check that the entity type is living, so just hope nothing goes wrong
        // the cast to Optional<?> is not optional, according to javac
        @SuppressWarnings({"unchecked", "RedundantCast"}) Optional<EntityType<? extends LivingEntity>> creeperType = (Optional<EntityType<? extends LivingEntity>>) (Optional<?>) Registries.ENTITY_TYPE.getOrEmpty(id);
        if (creeperType.isPresent()) {
            registerCreeperLike(id, creeperType.get());
        } else {
            CREEPER_LIKES.add(id);
        }
    }

    @ApiStatus.Internal
    public static void registerCreeperLike(Identifier id, EntityType<? extends LivingEntity> type) {
        String prefix = id.getNamespace().equals("minecraft") ? "" : (id.toString().replace(':', '_') + "_");
        EntityType<CreeperlingEntity> creeperlingType = Registry.register(
                Registries.ENTITY_TYPE,
                CreeperSpores.id(prefix + "creeperling"),
                createCreeperlingType(type)
        );
        CreeperSporeEffect sporesEffect = Registry.register(
                Registries.STATUS_EFFECT,
                CreeperSpores.id(prefix + "creeper_spore"),
                createCreeperSporesEffect(type)
        );
        CreeperEntry.register(type, creeperlingType, sporesEffect);
    }

    @Contract(pure = true)
    private static CreeperSporeEffect createCreeperSporesEffect(EntityType<?> creeperType) {
        return new CreeperSporeEffect(StatusEffectType.NEUTRAL, 0x22AA00, creeperType);
    }

    @Contract(pure = true)
    private static EntityType<CreeperlingEntity> createCreeperlingType(EntityType<? extends LivingEntity> creeperType) {
        Supplier<CreeperEntry> kind = Suppliers.memoize(() -> CreeperEntry.get(creeperType));
        DefaultAttributeContainer defaultAttributes = DefaultAttributeRegistry.get(creeperType);
        EntityType<CreeperlingEntity> creeperlingType = QuiltEntityTypeBuilder.createMob()
                .spawnGroup(creeperType.getSpawnGroup())
                .entityFactory((EntityType<CreeperlingEntity> type, World world) -> new CreeperlingEntity(Objects.requireNonNull(kind.get()), world))
                .setDimensions(EntityDimensions.changing(creeperType.getWidth() / 2f, creeperType.getHeight() / 2f))
                .maxBlockTrackingRange(64)
                .trackingTickInterval(1)
                .alwaysUpdateVelocity(true)
                .defaultAttributes(MobEntity.createAttributes()
                        .add(EntityAttributes.GENERIC_MAX_HEALTH, defaultAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) * 0.5)
                        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, defaultAttributes.getBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.8))
                .build();
        ((EntityTypeAccessor) creeperlingType).setTranslationKey("entity.creeperspores.creeperling");
        return creeperlingType;
    }
}
