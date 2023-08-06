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

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import org.ladysnake.creeperspores.CreeperEntry;
import org.ladysnake.creeperspores.common.CreeperSporeEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectSpriteManager.class)
public abstract class StatusEffectSpriteManagerMixin {
    @Unique
    private static final StatusEffect BASE_CREEPER_SPORES = CreeperEntry.getVanilla().sporeEffect();

    @Shadow public abstract Sprite getSprite(StatusEffect statusEffect_1);

    @Inject(method = "getSprite", at = @At("HEAD"), cancellable = true)
    private void creeperspores$getCreeperSporesSprite(StatusEffect effect, CallbackInfoReturnable<Sprite> cir) {
        if (effect instanceof CreeperSporeEffect && effect != BASE_CREEPER_SPORES) {
            cir.setReturnValue(getSprite(BASE_CREEPER_SPORES));
        }
    }
}
