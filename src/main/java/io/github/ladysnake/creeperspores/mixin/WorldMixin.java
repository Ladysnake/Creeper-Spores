package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.CreeperGrief;
import io.github.ladysnake.creeperspores.CreeperSpores;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow public abstract GameRules getGameRules();

    @ModifyVariable(method = "createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;Z)Lnet/minecraft/world/explosion/Explosion;", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private Explosion.DestructionType griefLessExplosion(Explosion.DestructionType explosionType, @Nullable Entity entity) {
        if (entity instanceof CreeperEntity creeper) {
            CreeperGrief grief = this.getGameRules().get(CreeperSpores.CREEPER_GRIEF).get();
            if (!grief.shouldGrief(creeper.isEnergySwirlActive())) {
                return Explosion.DestructionType.KEEP;
            }
        }
        return explosionType;
    }
}
