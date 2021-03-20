package io.github.ladysnake.creeperspores.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.Rule.class)
public interface GameRuleKeyAccessor {
    @Invoker("deserialize")
    void cspores$deserialize(String value);
}
