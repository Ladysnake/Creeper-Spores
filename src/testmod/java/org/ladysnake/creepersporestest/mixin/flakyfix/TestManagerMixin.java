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
package org.ladysnake.creepersporestest.mixin.flakyfix;

import net.minecraft.test.GameTestState;
import net.minecraft.test.TestManager;
import org.ladysnake.creepersporestest.flakyfix.FixedGameTestState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(TestManager.class)
public abstract class TestManagerMixin {
    @Shadow @Final private Collection<GameTestState> tests;

    // Ensure that when a test is restarted, it keeps track of its successor state
    @Inject(method = "start", at = @At("HEAD"))
    private void linkReplacementTests(GameTestState test, CallbackInfo ci) {
        this.tests.stream().filter(t -> t.getTestFunction() == test.getTestFunction()).findFirst().ifPresent(t -> ((FixedGameTestState) t).cs$setReplacementGameTest(test));
    }
}
