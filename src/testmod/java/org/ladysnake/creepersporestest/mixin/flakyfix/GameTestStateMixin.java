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
import org.ladysnake.creepersporestest.flakyfix.FixedGameTestState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameTestState.class)
public abstract class GameTestStateMixin implements FixedGameTestState {
    // Keep track of replacement states, for tests that are run multiple times
    private GameTestState cs$fallbackGameTest;

    @Override
    public void cs$setReplacementGameTest(GameTestState state) {
        this.cs$fallbackGameTest = state;
    }

    @Override
    public GameTestState cs$getReplacementGameTest() {
        return this.cs$fallbackGameTest == null ? (GameTestState) (Object) this : ((FixedGameTestState) this.cs$fallbackGameTest).cs$getReplacementGameTest();
    }
}
