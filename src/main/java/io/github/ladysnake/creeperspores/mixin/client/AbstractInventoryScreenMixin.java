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
package io.github.ladysnake.creeperspores.mixin.client;

import io.github.ladysnake.creeperspores.common.CreeperSporeEffect;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin {
    @Unique
    private List<StatusEffectInstance> renderedEffects;
    @Unique
    private int renderedEffectsIndex;

    @Inject(method = "drawStatusEffectDescriptions", at = @At("HEAD"))
    private void retrieveRenderedEffects(int x, int width, Iterable<StatusEffectInstance> effects, CallbackInfo ci) {
        renderedEffects = (List<StatusEffectInstance>) effects;
        renderedEffectsIndex = 0;
    }

    @ModifyVariable(method = "drawStatusEffectDescriptions", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private String updateRenderedEffectName(String drawnString) {
        StatusEffect renderedEffect = renderedEffects.get(renderedEffectsIndex++).getEffectType();
        if (renderedEffect instanceof CreeperSporeEffect) {
            return ((CreeperSporeEffect) renderedEffect).getLocalizedName();
        }
        return drawnString;
    }
}
