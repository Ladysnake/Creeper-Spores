package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.CreeperSpores;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity {

    private static final int MIN_SPORE_TIME = 20 * 60;

    @Shadow public abstract boolean isCharged();

    @Shadow private int explosionRadius;

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "explode", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private Explosion.DestructionType griefLessExplosion(Explosion.DestructionType explosionType) {
        if (!this.isCharged()) {
            return Explosion.DestructionType.NONE;
        }
        return explosionType;
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private void spreadSpores(CallbackInfo ci) {
        float radiusModifier = this.isCharged() ? 2f : 1f;
        float radius = this.explosionRadius * radiusModifier;
        List<LivingEntity> affectedEntities = this.world.getEntities(LivingEntity.class, this.getBoundingBox().expand(radius), entity -> this.distanceTo(entity) < radius && entity != this);
        for (LivingEntity affectedEntity : affectedEntities) {
             affectedEntity.addPotionEffect(new StatusEffectInstance(CreeperSpores.CREEPER_SPORES_EFFECT,  Math.round(MIN_SPORE_TIME * affectedEntity.distanceTo(this) / radiusModifier)));
        }
    }
}
