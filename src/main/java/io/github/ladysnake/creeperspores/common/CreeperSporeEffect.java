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
package io.github.ladysnake.creeperspores.common;

import io.github.ladysnake.creeperspores.CreeperSpores;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

import java.util.Objects;

public class CreeperSporeEffect extends StatusEffect {
    private final EntityType<?> creeperType;

    public CreeperSporeEffect(StatusEffectType type, int color, EntityType<?> creeperType) {
        super(type, color);
        this.creeperType = creeperType;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration == 1 && Math.random() < 0.6;
    }

    @Override
    public void applyUpdateEffect(LivingEntity affected, int amplifier) {
        spawnCreeperling(affected);
    }

    public void spawnCreeperling(Entity affected) {
        spawnCreeperling(affected, CreeperSpores.CREEPERLINGS.get(this.creeperType));
    }

    @Override
    public String loadTranslationKey() {
        return "effect.creeperspores.creeper_spore";
    }

    public String getLocalizedName() {
        return I18n.translate("effect.creeperspores.generic_spore", I18n.translate(this.creeperType.getTranslationKey()));
    }

    public static CreeperlingEntity spawnCreeperling(Entity affected, EntityType<? extends CreeperlingEntity> creeperlingType) {
        if (!affected.world.isClient) {
            CreeperlingEntity spawn = Objects.requireNonNull(creeperlingType.create(affected.world));
            spawn.setPositionAndAngles(affected.getX(), affected.getY(), affected.getZ(), 0, 0);
            affected.world.spawnEntity(spawn);
            return spawn;
        }
        return null;
    }
}
