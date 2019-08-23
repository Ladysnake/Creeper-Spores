/*
 * Creeper-Spores
 * Copyright (C) 2019 Ladysnake
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
            victim.addStatusEffect(new StatusEffectInstance(CreeperSpores.CREEPER_SPORES_EFFECT, (int) Math.round(MAX_SPORE_TIME * exposure)));
        }
        return affectedEntity;
    }
}
