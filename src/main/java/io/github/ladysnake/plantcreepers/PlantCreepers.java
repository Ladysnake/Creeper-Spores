package io.github.ladysnake.plantcreepers;

import io.github.ladysnake.plantcreepers.common.CreeperSporeEffect;
import io.github.ladysnake.plantcreepers.common.CreeperlingEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PlantCreepers implements ModInitializer {
    public static EntityType<CreeperlingEntity> CREEPERLING;
    public static StatusEffect CREEPER_SPORES_EFFECT;

    public static Identifier id(String path) {
        return new Identifier("plantcreepers", path);
    }

    @Override
    public void onInitialize() {
        CREEPERLING = Registry.register(
                Registry.ENTITY_TYPE,
                PlantCreepers.id("creeperling"),
                FabricEntityTypeBuilder.create(EntityCategory.MONSTER, CreeperlingEntity::new)
                        .size(EntityDimensions.changing(EntityType.CREEPER.getWidth() / 4f, EntityType.CREEPER.getHeight() / 4f))
                        .trackable(64, 1, true)
                        .build()
        );
        CREEPER_SPORES_EFFECT = Registry.register(Registry.STATUS_EFFECT, PlantCreepers.id("creeper_spore"), new CreeperSporeEffect(StatusEffectType.NEUTRAL, 0x22AA00));
    }
}
