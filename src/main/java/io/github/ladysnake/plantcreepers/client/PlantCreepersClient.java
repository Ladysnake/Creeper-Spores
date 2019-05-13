package io.github.ladysnake.plantcreepers.client;

import io.github.ladysnake.plantcreepers.common.CreeperlingEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;

public class PlantCreepersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(CreeperlingEntity.class, (r, it) -> new CreeperlingEntityRenderer(r));
    }
}
