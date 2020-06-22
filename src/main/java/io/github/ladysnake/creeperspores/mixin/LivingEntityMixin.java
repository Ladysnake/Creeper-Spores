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
package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.CreeperEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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

    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z", ordinal = 1))
    private void spawnCreeperling(DamageSource cause, float amount, CallbackInfoReturnable<Boolean> cir) {
        for (CreeperEntry creeperEntry : CreeperEntry.all()) {
            StatusEffectInstance spores = this.getStatusEffect(creeperEntry.sporeEffect);
            if (spores != null) {
                float chance = 0.2f * (spores.getAmplifier() + 1);
                if (this.getHealth() <= 0.0f) {
                    chance *= 4;
                }
                if (cause.isExplosive()) {
                    chance *= 2;
                }
                if (random.nextFloat() < chance) {
                    creeperEntry.spawnCreeperling(this);
                }
            }
        }
    }
}
