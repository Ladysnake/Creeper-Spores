package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.CreeperSpores;
import io.github.ladysnake.creeperspores.common.CreeperSporeEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract float getHealth();

    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(StatusEffect statusEffect_1);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getHealth()F", ordinal = 1))
    private void spawnCreeperling(DamageSource cause, float amount, CallbackInfoReturnable<Boolean> cir) {
        StatusEffectInstance sporesEffect = this.getStatusEffect(CreeperSpores.CREEPER_SPORES_EFFECT);
        if (sporesEffect != null) {
            float chance = 0.2f * (sporesEffect.getAmplifier() + 1);
            if (this.getHealth() <= 0.0f) {
                chance *= 4;
            }
            if (cause.isExplosive()) {
                chance *= 2;
            }
            if (random.nextFloat() < chance) {
                CreeperSporeEffect.spawnCreeperling(this);
            }
        }
    }

}
