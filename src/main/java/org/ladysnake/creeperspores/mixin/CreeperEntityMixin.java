/*
 * Creeper Spores
 * Copyright (C) 2019-2023 Ladysnake
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
package org.ladysnake.creeperspores.mixin;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.ladysnake.creeperspores.CreeperEntry;
import org.ladysnake.creeperspores.CreeperSpores;
import org.ladysnake.creeperspores.common.CreeperlingEntity;
import org.ladysnake.creeperspores.common.SporeSpreader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity implements SporeSpreader {

    @Unique private TriState giveSpores = TriState.DEFAULT;

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @Unique
    private boolean shouldSpreadSpores() {
        return this.giveSpores == TriState.TRUE || (this.giveSpores == TriState.DEFAULT && !this.isAiDisabled());
    }

    @Override
    public void spreadSpores(Explosion explosion, Vec3d center, Entity affectedEntity) {
        if (affectedEntity instanceof LivingEntity victim && this.shouldSpreadSpores()) {
            double exposure = Explosion.getExposure(center, victim);
            CreeperEntry creeperEntry = CreeperEntry.get(this.getType());
            if (creeperEntry != null) {
                victim.addStatusEffect(new StatusEffectInstance(creeperEntry.sporeEffect(), (int) Math.round(CreeperSpores.MAX_SPORE_TIME * exposure)));
            }
        }
    }

    @Inject(
            method = "interactMob",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/HostileEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"),
            cancellable = true
    )
    private void interactSpawnEgg(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = player.getStackInHand(hand);
        CreeperEntry creeperEntry = CreeperEntry.get(this.getType());
        if (creeperEntry != null && CreeperlingEntity.interactSpawnEgg(player, this, stack, creeperEntry)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeCustomDataToTag(NbtCompound tag, CallbackInfo ci) {
        if (this.giveSpores != TriState.DEFAULT) {
            tag.putBoolean(CreeperSpores.GIVE_SPORES_TAG, this.giveSpores.get());
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readCustomDataFromTag(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains(CreeperSpores.GIVE_SPORES_TAG)) {
            this.giveSpores = TriState.of(tag.getBoolean(CreeperSpores.GIVE_SPORES_TAG));
        }
    }
}
