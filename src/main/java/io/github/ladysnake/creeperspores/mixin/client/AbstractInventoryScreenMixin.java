package io.github.ladysnake.creeperspores.mixin.client;

import io.github.ladysnake.creeperspores.common.CreeperSporeEffect;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractInventoryScreen.class)
public class AbstractInventoryScreenMixin {
    @Unique
    private List<StatusEffectInstance> renderedEffects;
    @Unique
    private int renderedEffectsIndex;

    @Inject(method = "method_18644", at = @At("HEAD"))
    private void retrieveRenderedEffects(int x, int width, Iterable<StatusEffectInstance> effects, CallbackInfo ci) {
        renderedEffects = (List<StatusEffectInstance>) effects;
        renderedEffectsIndex = 0;
    }

    @ModifyVariable(method = "method_18644", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private String updateRenderedEffectName(String drawnString) {
        StatusEffect renderedEffect = renderedEffects.get(renderedEffectsIndex++).getEffectType();
        if (renderedEffect instanceof CreeperSporeEffect) {
            return ((CreeperSporeEffect) renderedEffect).getLocalizedName();
        }
        return drawnString;
    }
}
