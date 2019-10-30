package io.github.ladysnake.creeperspores.mixin;

import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityType.class)
public interface EntityTypeAccessor {
    @Accessor
    void setTranslationKey(String key);
}
