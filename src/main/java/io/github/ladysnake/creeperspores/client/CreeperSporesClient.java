package io.github.ladysnake.creeperspores.client;

import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;

public class CreeperSporesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(CreeperlingEntity.class, (r, it) -> new CreeperlingEntityRenderer(r));
    }
}
