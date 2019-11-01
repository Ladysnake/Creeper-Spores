package io.github.ladysnake.creeperspores.api;

import com.google.common.base.Preconditions;
import io.github.ladysnake.creeperspores.CreeperSpores;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.explosion.Explosion;

public final class CreeperSporesApi {

    /**
     * Register a registered {@link EntityType} as a creeper equivalent, able to spread spores and spawn creeperlings.
     *
     * <p> When an explosion's {@link Explosion#getCausingEntity() cause} is of a registered creeper-like type,
     * affected entities get a spore effect applied. The spore effect spawns creeperlings of the source type,
     * that eventually grow into regular entities of the appropriate type.
     *
     * <p> Because creeperlings take the texture of their parent type, entities with a different model than the base
     * creeper should use their own creeperling subclass and renderer.
     *
     * @param type a type of entity to consider creeper-like
     * @throws IllegalStateException if {@code type} has not been registered before calling this method
     */
    public static void registerCreeperLike(EntityType<?> type) {
        Preconditions.checkState(!Registry.ENTITY_TYPE.getId(type).equals(Registry.ENTITY_TYPE.getDefaultId()), "Entity types need to be registered first");
        CreeperSpores.registerCreeperLike(Registry.ENTITY_TYPE.getId(type), type);
    }
}
