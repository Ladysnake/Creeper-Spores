package io.github.ladysnake.creeperspores.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public interface SporeSpreader {
    void spreadSpores(Explosion explosion, Vec3d center, Entity affectedEntity);
}
