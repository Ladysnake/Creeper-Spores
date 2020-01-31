package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.common.gamerule.CSGamerules;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.class)
public abstract class GameRulesMixin {
    @SuppressWarnings({"UnresolvedMixinReference", "ResultOfMethodCallIgnored"})
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void initCustomGamerules(CallbackInfo ci) {
        CSGamerules.CREEPER_GRIEF.getClass();
    }
}
