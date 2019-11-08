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

import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;

public class CreeperlingChargeFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<CreeperlingEntity> creeperModel = new CreeperEntityModel<>(2.0F);

    public CreeperlingChargeFeatureRenderer(FeatureRendererContext<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> ctx) {
        super(ctx);
    }

    @Override
    protected float getEnergySwirlX(float v) {
        return v * 0.01F;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    @Override
    protected EntityModel<CreeperlingEntity> getEnergySwirlModel() {
        return this.creeperModel;
    }

}
