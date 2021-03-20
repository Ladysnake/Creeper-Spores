/*
 * Creeper Spores
 * Copyright (C) 2019-2021 Ladysnake
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

import io.github.ladysnake.creeperspores.CreeperEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Lazy;

import java.util.Objects;

public class CreeperSporeEffect extends StatusEffect {
    private final EntityType<?> creeperType;
    private final Lazy<CreeperEntry> creeperEntry;

    public CreeperSporeEffect(StatusEffectType type, int color, EntityType<?> creeperType) {
        super(type, color);
        this.creeperType = creeperType;
        this.creeperEntry = new Lazy<>(() -> Objects.requireNonNull(CreeperEntry.get(this.creeperType)));
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration == 1 && Math.random() < 0.6;
    }

    @Override
    public void applyUpdateEffect(LivingEntity affected, int amplifier) {
        this.creeperEntry.get().spawnCreeperling(affected);
    }

    @Override
    public String loadTranslationKey() {
        return "effect.creeperspores.creeper_spore";
    }

    public String getLocalizedName() {
        return I18n.translate("effect.creeperspores.generic_spore", I18n.translate(this.creeperType.getTranslationKey()));
    }

}
