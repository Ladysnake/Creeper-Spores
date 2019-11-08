package io.github.ladysnake.creeperspores.mixin.client;

import io.github.ladysnake.creeperspores.CreeperSpores;
import io.github.ladysnake.creeperspores.client.CreeperlingEntityRenderer;
import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRendererRegistry.class, remap = false)
public abstract class EntityRendererRegistryMixin {
    @Shadow public abstract void register(EntityType<?> entityType, EntityRendererRegistry.Factory factory);

    @Inject(method = "register", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;"))
    private void onRendererRegistered(EntityType<?> entityType, EntityRendererRegistry.Factory factory, CallbackInfo ci) {
        EntityType<CreeperlingEntity> creeperlingType = CreeperSpores.CREEPERLINGS.get(entityType);
        if (creeperlingType != null) {
            register(creeperlingType, (manager, context) -> CreeperlingEntityRenderer.createRenderer(manager, context, factory));
        }
    }

}
