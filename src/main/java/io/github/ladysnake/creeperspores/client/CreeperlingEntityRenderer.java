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

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ladysnake.creeperspores.CreeperSpores;
import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import it.unimi.dsi.fastutil.Hash;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CreeperlingEntityRenderer extends MobEntityRenderer<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper.png");
    private static final EntityType<? extends CreeperlingEntity> BASE_CREEPERLING = CreeperSpores.CREEPERLINGS.get(EntityType.CREEPER);
    private static final Map<EntityType<?>, Identifier> CREEPER_TEXTURES = new HashMap<>(CreeperSpores.CREEPERLINGS.size(), Hash.VERY_FAST_LOAD_FACTOR);

    static {
        if (FabricLoader.getInstance().isModLoaded("mobz")) {
            CREEPER_TEXTURES.put(CreeperSpores.CREEPERLINGS.get(Registry.ENTITY_TYPE.get(new Identifier("mobz", "creep_entity"))), new Identifier("mobz", "textures/entity/creep.png"));
            CREEPER_TEXTURES.put(CreeperSpores.CREEPERLINGS.get(Registry.ENTITY_TYPE.get(new Identifier("mobz", "crip_entity"))), new Identifier("mobz", "textures/entity/crip.png"));
        }
    }

    public CreeperlingEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, new CreeperEntityModel<>(), 0.25F);
        this.addFeature(new CreeperlingChargeFeatureRenderer(this));
    }

    @Override
    protected void scale(CreeperlingEntity entity, MatrixStack matrix, float tickDelta) {
        matrix.scale(0.5f, 0.5f, 0.5f);
    }

    @Nullable
    @Override
    public Identifier getTexture(CreeperlingEntity creeperling) {
        // fast track for most common case
        if (creeperling.getType() == BASE_CREEPERLING) {
            return SKIN;
        }
        return CREEPER_TEXTURES.getOrDefault(creeperling.getType(), SKIN);
    }
}
