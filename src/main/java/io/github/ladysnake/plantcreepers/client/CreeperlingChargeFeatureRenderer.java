package io.github.ladysnake.plantcreepers.client;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.ladysnake.plantcreepers.common.CreeperlingEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.util.Identifier;

public class CreeperlingChargeFeatureRenderer extends FeatureRenderer<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<CreeperlingEntity> creeperModel = new CreeperEntityModel<>(2.0F);

    public CreeperlingChargeFeatureRenderer(FeatureRendererContext<CreeperlingEntity, CreeperEntityModel<CreeperlingEntity>> ctx) {
        super(ctx);
    }

    @Override
    public void render(CreeperlingEntity creeper, float float_1, float float_2, float float_3, float float_4, float float_5, float float_6, float float_7) {
        if (creeper.isCharged()) {
            boolean boolean_1 = creeper.isInvisible();
            GlStateManager.depthMask(!boolean_1);
            this.bindTexture(SKIN);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float age = (float)creeper.age + float_3;
            GlStateManager.translatef(age * 0.01F, age * 0.01F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();
            GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            this.getModel().copyStateTo(this.creeperModel);
            GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
            gameRenderer.setFogBlack(true);
            this.creeperModel.render(creeper, float_1, float_2, float_4, float_5, float_6, float_7);
            gameRenderer.setFogBlack(false);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        }
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
