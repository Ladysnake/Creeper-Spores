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
import io.github.ladysnake.creeperspores.CreeperSpores;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin {
    @ModifyVariable(method = "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;refreshPositionAndAngles(DDDFF)V", shift = AFTER))
    private static MobEntity substituteCreeper(MobEntity spawnedEntity) {
        if (spawnedEntity instanceof CreeperEntity
                && spawnedEntity.world.getLightLevel(LightType.SKY, spawnedEntity.getBlockPos()) > 0
                && spawnedEntity.world.getGameRules().get(CreeperSpores.CREEPER_REPLACE_CHANCE).get() > spawnedEntity.getRandom().nextDouble()) {
            CreeperEntry creeperEntry = CreeperEntry.get(spawnedEntity.getType());
            if (creeperEntry != null) {
                return creeperEntry.createCreeperling(spawnedEntity);
            }
        }
        return spawnedEntity;
    }
}
