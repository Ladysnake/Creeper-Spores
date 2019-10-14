/*
 * Creeper-Spores
 * Copyright (C) 2019 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package io.github.ladysnake.creeperspores.mixin;

import io.github.ladysnake.creeperspores.CreeperSpores;
import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import io.github.ladysnake.creeperspores.common.SporeSpreader;
import io.github.ladysnake.creeperspores.gamerule.CreeperGrief;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity implements SporeSpreader {

    @Unique private TriState giveSpores = TriState.DEFAULT;

    @Shadow @Final private static TrackedData<Boolean> CHARGED;

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @Unique
    private boolean shouldSpreadSpores() {
        return this.giveSpores == TriState.TRUE || (this.giveSpores == TriState.DEFAULT && !this.isAiDisabled());
    }

    @Override
    public void spreadSpores(Explosion explosion, Vec3d center, Entity affectedEntity) {
        if (affectedEntity instanceof LivingEntity && this.shouldSpreadSpores()) {
            LivingEntity victim = ((LivingEntity) affectedEntity);
            double exposure = Explosion.getExposure(center, victim);
            victim.addPotionEffect(new StatusEffectInstance(CreeperSpores.CREEPER_SPORES_EFFECTS.get(this.getType()), (int) Math.round(CreeperSpores.MAX_SPORE_TIME * exposure)));
        }
    }

    @Inject(
            method = "interactMob",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/HostileEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Z"),
            cancellable = true
    )
    private void interactSpawnEgg(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = player.getStackInHand(hand);
        EntityType<CreeperlingEntity> creeperlingType = CreeperSpores.CREEPERLINGS.get(this.getType());
        if (creeperlingType != null && CreeperlingEntity.interactSpawnEgg(player, this, creeperlingType, stack)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "explode", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private Explosion.DestructionType griefLessExplosion(Explosion.DestructionType explosionType) {
        CreeperGrief grief = world.getGameRules().get(CreeperSpores.CREEPER_GRIEF).get();
        if (!grief.shouldGrief(this.dataTracker.get(CHARGED))) {
            return Explosion.DestructionType.NONE;
        }
        return explosionType;
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        if (this.giveSpores != TriState.DEFAULT) {
            tag.putBoolean(CreeperSpores.GIVE_SPORES_TAG, this.giveSpores.get());
        }
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
        if (tag.containsKey(CreeperSpores.GIVE_SPORES_TAG)) {
            this.giveSpores = TriState.of(tag.getBoolean(CreeperSpores.GIVE_SPORES_TAG));
        }
    }
}
