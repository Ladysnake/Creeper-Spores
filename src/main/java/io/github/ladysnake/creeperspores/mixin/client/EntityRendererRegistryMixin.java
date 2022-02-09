/*
 * Creeper Spores
 * Copyright (C) 2019-2022 Ladysnake
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

import io.github.ladysnake.creeperspores.CreeperEntry;
import io.github.ladysnake.creeperspores.client.CreeperlingEntityRenderer;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRendererRegistryImpl.class, remap = false)
public abstract class EntityRendererRegistryMixin {
    @Shadow
    public static <E extends Entity> void register(EntityType<?> entityType, EntityRendererFactory<E> factory) {
    }

    @Inject(method = "register", at = @At(value = "RETURN"))
    private static <E extends Entity> void onRendererRegistered(EntityType<? extends E> entityType, EntityRendererFactory<E> factory, CallbackInfo ci) {
        CreeperEntry creeperEntry = CreeperEntry.get(entityType);
        if (creeperEntry != null) {
            register(creeperEntry.creeperlingType(), (context) -> CreeperlingEntityRenderer.createRenderer(context, factory));
        }
    }
}
