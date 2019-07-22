/*
 * Plant-Creepers
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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HostileEntity.class)
public abstract class HostileEntityMixin extends MobEntityWithAi {
    protected HostileEntityMixin(EntityType<? extends MobEntityWithAi> type, World world) {
        super(type, world);
    }

    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntityWithAi;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    private float dealDoubleFireDamage(DamageSource damage, float damageAmount) {
        if ((MobEntityWithAi) this instanceof CreeperEntity && damage.isFire()) {
            return damageAmount * 2;
        }
        return damageAmount;
    }
}
