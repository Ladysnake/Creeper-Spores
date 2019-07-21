package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.CreeperSpores;
import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
    @ModifyVariable(method = "spawnEntitiesInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;setPositionAndAngles(DDDFF)V", shift = AFTER))
    private static MobEntity substituteCreeper(MobEntity spawnedEntity) {
        if (spawnedEntity instanceof CreeperEntity && spawnedEntity.world.getLightLevel(LightType.SKY, spawnedEntity.getBlockPos()) > 0) {
            CreeperlingEntity substitute = new CreeperlingEntity(CreeperSpores.CREEPERLING, spawnedEntity.world);
            substitute.copyPositionAndRotation(spawnedEntity);
            return substitute;
        }
        return spawnedEntity;
    }
}
