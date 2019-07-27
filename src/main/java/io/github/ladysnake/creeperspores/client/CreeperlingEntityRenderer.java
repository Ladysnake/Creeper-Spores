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
package io.github.ladysnake.creeperspores.client;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class CreeperlingEntityRenderer extends MobEntityRenderer<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper.png");

    public CreeperlingEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, new CreeperEntityModel<>(), 0.25F);
        this.addFeature(new CreeperlingChargeFeatureRenderer(this));
    }

    @Override
    protected void render(CreeperlingEntity creeper, float x, float y, float z, float yaw, float pitch, float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.5F, 0.5F, 0.5F);
        GlStateManager.translatef(0.0F, 24.0F * tickDelta, 0.0F);
        super.render(creeper, x, y, z, yaw, pitch, tickDelta);
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected Identifier getTexture(CreeperlingEntity var1) {
        return SKIN;
    }
}
