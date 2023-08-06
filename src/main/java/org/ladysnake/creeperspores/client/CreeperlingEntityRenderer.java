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
package org.ladysnake.creeperspores.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.ladysnake.creeperspores.common.CreeperlingEntity;
import org.ladysnake.creeperspores.mixin.client.EntityRendererAccessor;

import javax.annotation.Nullable;

public class CreeperlingEntityRenderer extends MobEntityRenderer<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> {
    public static final Identifier DEFAULT_SKIN = new Identifier("textures/entity/creeper/creeper.png");

    private final Identifier texture;

    public static <E extends Entity> EntityRenderer<CreeperlingEntity> createRenderer(EntityRendererFactory.Context context, EntityRendererFactory<E> factory) {
        EntityRenderer<?> baseRenderer = factory.create(context);
        Identifier texture;
        try {
            texture = ((EntityRendererAccessor) baseRenderer).invokeGetTexture(null);
        } catch (NullPointerException ignored) {
            // This creeper renderer does not like nulls, fall back to default texture
            texture = DEFAULT_SKIN;
        }
        return new CreeperlingEntityRenderer(context, texture);
    }

    public CreeperlingEntityRenderer(EntityRendererFactory.Context context, Identifier texture) {
        super(context, new CreeperEntityModel<>(context.getPart(EntityModelLayers.CREEPER)), 0.25F);
        this.addFeature(new CreeperlingChargeFeatureRenderer(this, context.getModelLoader()));
        this.texture = texture;
    }

    @Override
    protected void scale(CreeperlingEntity entity, MatrixStack matrix, float tickDelta) {
        matrix.scale(0.5f, 0.5f, 0.5f);
    }

    @Nullable
    @Override
    public Identifier getTexture(CreeperlingEntity creeperling) {
        return texture;
    }
}
