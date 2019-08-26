package io.github.ladysnake.creeperspores.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.class)
public interface GameRulesAccessor {
    @Invoker
    @SuppressWarnings("PublicStaticMixinMember")
    static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> invokeRegister(String name, GameRules.RuleType<T> type) {
        throw new AssertionError();
    }
}
