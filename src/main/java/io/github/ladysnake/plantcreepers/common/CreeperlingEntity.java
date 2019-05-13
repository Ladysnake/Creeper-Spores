package io.github.ladysnake.plantcreepers.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

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
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
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
            adult.copyPositionAndRotation(this);
            world.spawnEntity(adult);
            this.remove();
        }
    }
}
