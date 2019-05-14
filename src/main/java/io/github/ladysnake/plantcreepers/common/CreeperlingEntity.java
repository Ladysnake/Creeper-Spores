package io.github.ladysnake.plantcreepers.common;

import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

import java.util.UUID;

public class CreeperlingEntity extends MobEntityWithAi {
    private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final int MATURATION_TIME = 20 * 60 * 10;

    public CreeperlingEntity(EntityType<? extends CreeperlingEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, OcelotEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, CatEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, PlayerEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
    }

    @Override
    public boolean canSpawn(IWorld iWorld_1, SpawnType spawnType_1) {
        return super.canSpawn(iWorld_1, spawnType_1) && world.getLightLevel(LightType.SKY, this.getBlockPos()) > 0;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, ViewableWorld worldView) {
        // Creeperlings like sunlight
        int skyLightLevel = worldView.getLightLevel(LightType.SKY, pos);
        float skyFavor = worldView.getDimension().getLightLevelToBrightness()[skyLightLevel] * 3.0F;
        // But they can do with artificial light if there is not anything better
        float favor = Math.max(worldView.getBrightness(pos) - 0.5F, skyFavor);
        // They like good soils too
        if (BlockTags.DIRT_LIKE.contains(worldView.getBlockState(pos.down()).getBlock())) {
            favor += 3.0F;
        }
        // What they really want is camouflage
        Material material = worldView.getBlockState(pos).getMaterial();
        if (material == Material.PLANT || material == Material.REPLACEABLE_PLANT) {
            favor += 4.0F;
        }
        return favor;
    }

    public boolean isCharged() {
        return this.dataTracker.get(CHARGED);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHARGED, false);
    }

    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.isCharged()) {
            tag.putBoolean("powered", true);
        }
    }

    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.dataTracker.set(CHARGED, tag.getBoolean("powered"));
    }

    public void onStruckByLightning(LightningEntity lightningEntity_1) {
        super.onStruckByLightning(lightningEntity_1);
        this.dataTracker.set(CHARGED, true);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient && this.age >= MATURATION_TIME && this.world.getDifficulty() != Difficulty.PEACEFUL) {
            CreeperEntity adult = new CreeperEntity(EntityType.CREEPER, world);
            UUID adultUuid = adult.getUuid();
            adult.fromTag(this.toTag(new CompoundTag()));
            adult.setUuid(adultUuid);
            world.spawnEntity(adult);
            this.remove();
        }
    }
}
