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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class CreeperlingChargeFeatureRenderer extends FeatureRenderer<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<CreeperlingEntity> creeperModel = new CreeperEntityModel<>(2.0F);

    public CreeperlingChargeFeatureRenderer(FeatureRendererContext<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> ctx) {
        super(ctx);
    }

    @Override
    public void render(CreeperlingEntity creeper, float f1, float f2, float tickDelta, float f4, float f5, float f6, float f7) {
        if (creeper.isCharged()) {
            boolean boolean_1 = creeper.isInvisible();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            GlStateManager.translatef(0.0F, 1.5f, 0.0F);
            GlStateManager.depthMask(!boolean_1);
            this.bindTexture(SKIN);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            float age = (float)creeper.age + tickDelta;
            GlStateManager.translatef(age * 0.01F, age * 0.01F, 0.0F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.enableBlend();
            GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            this.getModel().copyStateTo(this.creeperModel);
            GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
            gameRenderer.setFogBlack(true);
            this.creeperModel.render(creeper, f1, f2, f4, f5, f6, f7);
            gameRenderer.setFogBlack(false);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
