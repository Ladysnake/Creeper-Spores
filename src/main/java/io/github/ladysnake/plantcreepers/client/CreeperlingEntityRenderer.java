package io.github.ladysnake.plantcreepers.client;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.ladysnake.plantcreepers.common.CreeperlingEntity;
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
    protected void render(CreeperlingEntity creeper, float float_1, float float_2, float float_3, float float_4, float float_5, float float_6) {
        GlStateManager.scalef(0.2f, 0.2f, 0.2f);
        super.render(creeper, float_1, float_2, float_3, float_4, float_5, float_6);
        GlStateManager.scalef(1f, 1f, 1f);
    }

    @Nullable
    @Override
    protected Identifier getTexture(CreeperlingEntity var1) {
        return SKIN;
    }
}
