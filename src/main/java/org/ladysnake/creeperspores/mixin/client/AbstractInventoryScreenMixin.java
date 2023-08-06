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
package org.ladysnake.creeperspores.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.MutableText;
import org.ladysnake.creeperspores.common.CreeperSporeEffect;
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
    private void creeperspores$retrieveRenderedEffects(GuiGraphics graphics, int x, int height, Iterable<StatusEffectInstance> effects, CallbackInfo ci) {
        renderedEffects = (List<StatusEffectInstance>) effects;
        renderedEffectsIndex = 0;
    }

    @Inject(method = "drawStatusEffectDescriptions", at = @At("RETURN"))
    private void creeperspores$clearRenderedEffects(GuiGraphics graphics, int x, int height, Iterable<StatusEffectInstance> effects, CallbackInfo ci) {
        renderedEffects = null;
    }

    @ModifyVariable(method = "getStatusEffectName", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/text/Text;copy()Lnet/minecraft/text/MutableText;"), index = 2)
    private MutableText creeperspores$updateRenderedEffectName(MutableText drawnString) {
        if (renderedEffects != null) {
            StatusEffect renderedEffect = renderedEffects.get(renderedEffectsIndex++).getEffectType();
            if (renderedEffect instanceof CreeperSporeEffect sporeEffect) {
                return sporeEffect.getLocalizedName().copyContentOnly();
            }
        }
        return drawnString;
    }
}
