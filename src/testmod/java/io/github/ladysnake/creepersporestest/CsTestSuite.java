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
package io.github.ladysnake.creepersporestest;

import io.github.ladysnake.creeperspores.CreeperEntry;
import io.github.ladysnake.creeperspores.common.CreeperlingEntity;
import io.github.ladysnake.creepersporestest.mixin.CreeperEntityAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.quiltmc.qsl.testing.api.game.QuiltGameTest;

public final class CsTestSuite implements QuiltGameTest {

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void explosionsTweaksWork(TestContext ctx) {
        var creeper = ctx.spawnMob(EntityType.CREEPER, 1, 0, 1);
        var golem = ctx.spawnMob(EntityType.IRON_GOLEM, 5, 0, 5);

        ctx.succeedIf(() -> {
            ctx.expectBlock(Blocks.DIRT, 1, -1, 1);
            ((CreeperEntityAccessor) creeper).cs$explode();
            ctx.expectBlock(Blocks.DIRT, 1, -1, 1); // no grief
            if (!golem.hasStatusEffect(CreeperEntry.getVanilla().sporeEffect())) {
                throw new GameTestException("Expected creeper to spread spores");
            }
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE, timeout = 5)
    public void creeperSporesSpawnCreeperlingsOnDamage(TestContext ctx) {
        IronGolemEntity golem = ctx.spawnMob(EntityType.IRON_GOLEM, 1, 0, 1);
        // Spores Level 5 gives a 100% chance to spawn creeperlings on death
        golem.addStatusEffect(new StatusEffectInstance(CreeperEntry.getVanilla().sporeEffect(), 5, 4));
        golem.damage(golem.getWorld().getDamageSources().dragonBreath(), 0);
        ctx.succeedIf(() -> ctx.expectEntity(CreeperEntry.getVanilla().creeperlingType()));
    }

    // 60% chance to spawn a creeper => 1 in 10k to fail 10 attempts in a row
    @GameTest(structureName = EMPTY_STRUCTURE, maxAttempts = 10, timeout = 2)
    public void creeperSporesSpawnCreeperlingsOnComplete(TestContext ctx) {
        IronGolemEntity golem = ctx.spawnMob(EntityType.IRON_GOLEM, 1, 0, 1);
        golem.addStatusEffect(new StatusEffectInstance(CreeperEntry.getVanilla().sporeEffect(), 1));
        ctx.succeedWhen(() -> ctx.expectEntity(CreeperEntry.getVanilla().creeperlingType()));
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void creeperlingsGrowWithBonemeal(TestContext ctx) {
        CreeperlingEntity creeperling = ctx.spawnMob(CreeperEntry.getVanilla().creeperlingType(), 1, 0, 1);
        creeperling.setCustomName(Text.of("Bobby"));
        PlayerEntity player = ctx.createMockPlayer();
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BONE_MEAL, 64));
        creeperling.interact(player, Hand.MAIN_HAND);

        ctx.waitAndRun(1, () -> {
            // A single bone meal should not cause the creeper to grow up
            ctx.expectEntityWithData(new BlockPos(1, 0, 1), CreeperEntry.getVanilla().creeperlingType(), Entity::getCustomName, Text.of("Bobby"));
            for (int i = 0; i < 7; i++) {
                creeperling.interact(player, Hand.MAIN_HAND);
            }

            ctx.waitAndRun(1, () -> ctx.succeedIf(() -> {
                ctx.expectNoEntity(CreeperEntry.getVanilla().creeperlingType());
                ctx.expectEntityWithData(new BlockPos(1, 0, 1), EntityType.CREEPER, Entity::getCustomName, Text.of("Bobby"));
            }));
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void creeperlingsGrowInstantlyWithSuperFertilizer(TestContext ctx) {
        CreeperlingEntity creeperling = ctx.spawnMob(CreeperEntry.getVanilla().creeperlingType(), 1, 0, 1);
        creeperling.setCustomName(Text.of("Bobby"));
        PlayerEntity player = ctx.createMockPlayer();
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.PORKCHOP, 1));
        creeperling.interact(player, Hand.MAIN_HAND);
        ctx.succeedAtTickIf(1, () -> {
            ctx.expectNoEntity(CreeperEntry.getVanilla().creeperlingType());
            ctx.expectEntityWithData(new BlockPos(1, 0, 1), EntityType.CREEPER, Entity::getCustomName, Text.of("Bobby"));
            ctx.complete();
        });
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void flintAndSteelSetsCreeperlingsOnFire(TestContext ctx) {
        CreeperlingEntity creeperling = ctx.spawnMob(CreeperEntry.getVanilla().creeperlingType(), 1, 0, 1);
        creeperling.setCustomName(Text.of("Bobby"));
        PlayerEntity player = ctx.createMockPlayer();
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.FLINT_AND_STEEL, 1));
        creeperling.interact(player, Hand.MAIN_HAND);
        ctx.succeedIf(creeperling::isOnFire);
    }

    @GameTest(structureName = EMPTY_STRUCTURE, timeout = 5)
    public void creepersBurnMore(TestContext ctx) {
        var creeper = ctx.spawnMob(EntityType.CREEPER, 1, 0, 1);
        var golem = ctx.spawnMob(EntityType.IRON_GOLEM, 2, 0, 2);
        creeper.setOnFireFor(2);
        golem.setOnFireFor(2);
        ctx.succeedWhen(() -> {
            if (creeper.getHealth() != creeper.getMaxHealth() - 2) {
                throw new GameTestException("Expected creeper to take a heart of damage from fire");
            }
            if (golem.getHealth() != golem.getMaxHealth() - 1) {
                throw new GameTestException("Expected golem to take half a heart of damage from fire");
            }
        });
    }
}
