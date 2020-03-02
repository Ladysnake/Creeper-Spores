/*
 * Creeper-Spores
 * Copyright (C) 2019-2020 Ladysnake
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
import io.github.ladysnake.creeperspores.mixin.EntityTypeAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.function.BiConsumer;

public class CreeperSpores implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("creeper-spores");

    /** Identifiers corresponding to entity types that should be {@linkplain #registerCreeperLike(Identifier, EntityType)
        registered as creeper likes} if and when the entity type gets registered to {@link Registry#ENTITY_TYPE}.*/
    public static final Set<Identifier> CREEPER_LIKES = new HashSet<>(Arrays.asList(
            new Identifier("minecraft", "creeper"),
            new Identifier("mobz", "creep_entity"),
            new Identifier("mobz", "crip_entity")
    ));

    public static final Tag<Item> FERTILIZERS = TagRegistry.item(new Identifier("fabric", "fertilizers"));

    public static final Identifier CREEPERLING_FERTILIZATION_PACKET = id("creeperling-fertilization");
    public static final String GIVE_SPORES_TAG = "cspores:giveSpores";
    public static final int MAX_SPORE_TIME = 20 * 180;

    public static Identifier id(String path) {
        return new Identifier("creeperspores", path);
    }

    public static <T> void visitRegistry(Registry<T> registry, BiConsumer<Identifier, T> visitor) {
        registry.getIds().forEach(id -> visitor.accept(id, registry.get(id)));
        RegistryEntryAddedCallback.event(registry).register((index, identifier, entry) -> visitor.accept(identifier, entry));
    }

    @Override
    public void onInitialize() {
        visitRegistry(Registry.ENTITY_TYPE, (id, type) -> {
            if (CREEPER_LIKES.contains(id)) {
                registerCreeperLike(id, type);
            }
        });
    }

    @ApiStatus.Internal
    public static void registerCreeperLike(Identifier id) {
        Optional<EntityType<?>> creeperType = Registry.ENTITY_TYPE.getOrEmpty(id);
        if (creeperType.isPresent()) {
            registerCreeperLike(id, creeperType.get());
        } else {
            CREEPER_LIKES.add(id);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiStatus.Internal
    public static void registerCreeperLike(Identifier id, EntityType type) {
        String prefix = id.getNamespace().equals("minecraft") ? "" : (id.toString().replace(':', '_') + "_");
        EntityType<CreeperlingEntity> creeperlingType = Registry.register(
                Registry.ENTITY_TYPE,
                CreeperSpores.id(prefix + "creeperling"),
                createCreeperlingType(type)
        );
        CreeperSporeEffect sporesEffect = Registry.register(
                Registry.STATUS_EFFECT,
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
    private static EntityType<CreeperlingEntity> createCreeperlingType(EntityType<?> creeperType) {
        Lazy<CreeperEntry> kind = new Lazy<>(() -> Objects.requireNonNull(CreeperEntry.get(creeperType)));
        EntityType<CreeperlingEntity> creeperlingType = FabricEntityTypeBuilder
                .create(creeperType.getCategory(),
                        (EntityType<CreeperlingEntity> type, World world) -> new CreeperlingEntity(kind.get(), world))
                .size(EntityDimensions.changing(creeperType.getWidth() / 2f, creeperType.getHeight() / 2f))
                .trackable(64, 1, true)
                .build();
        ((EntityTypeAccessor) creeperlingType).setTranslationKey("entity.creeperspores.creeperling");
        return creeperlingType;
    }
}
