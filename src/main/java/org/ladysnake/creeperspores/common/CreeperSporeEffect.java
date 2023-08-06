/*
 * Creeper Spores
 * Copyright (C) 2019-2023 Ladysnake
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
package org.ladysnake.creeperspores.common;

import com.google.common.base.Suppliers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.text.Text;
import org.ladysnake.creeperspores.CreeperEntry;

import java.util.Objects;
import java.util.function.Supplier;

public class CreeperSporeEffect extends StatusEffect {
    private final EntityType<?> creeperType;
    private final Supplier<CreeperEntry> creeperEntry;

    public CreeperSporeEffect(StatusEffectType type, int color, EntityType<?> creeperType) {
        super(type, color);
        this.creeperType = creeperType;
        this.creeperEntry = Suppliers.memoize(() -> Objects.requireNonNull(CreeperEntry.get(this.creeperType)));
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

    public Text getLocalizedName() {
        return Text.translatable("effect.creeperspores.generic_spore", this.creeperType.getName());
    }

}
