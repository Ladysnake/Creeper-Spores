package io.github.ladysnake.creeperspores.mixin;

import com.mojang.serialization.DynamicLike;
import io.github.ladysnake.creeperspores.CreeperSpores;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GameRules.class)
public abstract class GameRulesMixin {
    @Shadow @Final private Map<GameRules.Key<?>, GameRules.Rule<?>> rules;

    @Inject(method = "load", at = @At("RETURN"))
    private void loadOldGamerules(DynamicLike<?> dynamicLike, CallbackInfo ci) {
        dynamicLike.get("cspores_creeperGrief").asString().result().ifPresent(((GameRuleKeyAccessor) this.rules.get(CreeperSpores.CREEPER_GRIEF))::cspores$deserialize);
    }
}
