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
package io.github.ladysnake.creeperspores.common;

import io.github.ladysnake.creeperspores.CreeperSpores;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class CreeperSporeEffect extends StatusEffect {
    public CreeperSporeEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int frequency = 500 >> amplifier;
        if (frequency > 0) {
            return duration % frequency == 0 && Math.random() > 0.5;
        } else {
            return true;
        }
    }

    @Override
    public void applyUpdateEffect(LivingEntity affected, int amplifier) {
        spawnCreeperling(affected);
    }

    public static void spawnCreeperling(Entity affected) {
        if (!affected.world.isClient) {
            CreeperlingEntity spawn = new CreeperlingEntity(CreeperSpores.CREEPERLING, affected.world);
            spawn.setPosition(affected.x, affected.y, affected.z);
            affected.world.spawnEntity(spawn);
        }
    }
}
