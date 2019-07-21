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
        super(dispatcher, new CreeperEntityModel<>(), 0.5F);
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
