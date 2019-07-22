package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {
    @Inject(method = "useOnFertilizable", at = @At("RETURN"), cancellable = true)
    private static void fertilizeCreeperlings(ItemStack boneMeal, World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            List<CreeperlingEntity> creeperlings = world.getEntities(CreeperlingEntity.class, new Box(pos));
            if (!creeperlings.isEmpty()) {
                creeperlings.get(0).applyFertilizer();
                boneMeal.decrement(1);
                cir.setReturnValue(true);
            }
        }
    }
}
