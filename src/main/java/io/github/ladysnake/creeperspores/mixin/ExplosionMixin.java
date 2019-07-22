package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.CreeperSpores;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow public static float getExposure(Vec3d vec3d_1, Entity entity_1) { throw new AssertionError(); }

    @Shadow @Final private double x;

    @Shadow @Final private double y;

    @Shadow @Final private double z;

    @Shadow @Final private Entity entity;

    @Unique private static final int MAX_SPORE_TIME = 20 * 180;

    // Using ModifyVariable is way easier than an Inject capturing every local
    @ModifyVariable(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private Entity spreadSpores(Entity affectedEntity) {
        if (this.entity instanceof CreeperEntity && affectedEntity instanceof LivingEntity) {
            LivingEntity victim = ((LivingEntity) affectedEntity);
            Vec3d center = new Vec3d(this.x, this.y, this.z);
            double exposure = getExposure(center, victim);
            victim.addPotionEffect(new StatusEffectInstance(CreeperSpores.CREEPER_SPORES_EFFECT, (int) Math.round(MAX_SPORE_TIME * exposure)));
        }
        return affectedEntity;
    }
}
