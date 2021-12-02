/*
 * Creeper Spores
 * Copyright (C) 2019-2021 Ladysnake
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
package io.github.ladysnake.creepersporestest.mixin.flakyfix;

import io.github.ladysnake.creepersporestest.flakyfix.FixedGameTestState;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

@Mixin(TestSet.class)
public abstract class TestSetMixin {
    @Shadow @Final private Collection<GameTestState> tests;

    @Inject(method = "isDone", at = @At("HEAD"))
    private void replaceTestStates(CallbackInfoReturnable<Boolean> cir) {
        for (ListIterator<GameTestState> it = ((List<GameTestState>)this.tests).listIterator(); it.hasNext(); ) {
            GameTestState test = it.next();
            GameTestState replacement = ((FixedGameTestState) test).cs$getReplacementGameTest();
            if (replacement != test) {
                it.set(replacement);
            }
        }
    }
}
