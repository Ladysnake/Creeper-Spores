/*
 * Creeper Spores
 * Copyright (C) 2019-2022 Ladysnake
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
package io.github.ladysnake.creeperspores.common;

import io.github.ladysnake.creeperspores.CreeperEntry;
import io.github.ladysnake.creeperspores.CreeperSpores;
import io.github.ladysnake.creeperspores.mixin.EntityAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Material;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class CreeperlingEntity extends PathAwareEntity implements SkinOverlayOwner {
    private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(CreeperlingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final int MATURATION_TIME = 20 * 60 * 8;

    private final CreeperEntry kind;

    private int ticksInSunlight = 0;
    private boolean trusting;
    private FleeEntityGoal<PlayerEntity> fleeGoal;

    public CreeperlingEntity(CreeperEntry kind, World world) {
        super(kind.creeperlingType(), world);
        this.kind = kind;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, OcelotEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, CatEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.add(3, new TemptGoal(this, 0.3D, Ingredient.fromTag(CreeperSpores.FERTILIZERS), false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.setTrusting(false);
    }

    @Override
    public boolean shouldRenderOverlay() {
        return this.isCharged();
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnType) {
        return super.canSpawn(world, spawnType) && this.world.getLightLevel(LightType.SKY, this.getBlockPos()) > 0;
    }

    public boolean isTrusting() {
        return trusting;
    }

    public void setTrusting(boolean trusting) {
        this.trusting = trusting;
        if (this.fleeGoal == null) {
            this.fleeGoal = new FleeEntityGoal<>(this, PlayerEntity.class, 6.0F, 1.0D, 1.2D);
        }
        if (trusting) {
            this.goalSelector.remove(fleeGoal);
        } else {
            this.goalSelector.add(4, fleeGoal);
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack held = player.getStackInHand(hand);
        if (CreeperSpores.FERTILIZERS.contains(held.getItem())) {
            if (!world.isClient) {
                this.applyFertilizer(held);
                this.setTrusting(true);
            }
            return ActionResult.SUCCESS;
        } else {
            if (interactSpawnEgg(player, this, held, this.kind)) {
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    public static boolean interactSpawnEgg(PlayerEntity player, Entity interacted, ItemStack stack, CreeperEntry kind) {
        Item item = stack.getItem();
        if (item instanceof SpawnEggItem && ((SpawnEggItem)item).getEntityType(stack.getNbt()) == EntityType.CREEPER) {
            if (!interacted.world.isClient) {
                CreeperlingEntity creeperling = kind.spawnCreeperling(interacted);
                if (creeperling != null) {
                    if (stack.hasCustomName()) {
                        creeperling.setCustomName(stack.getName());
                    }

                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void applyFertilizer(ItemStack boneMeal) {
        if (!this.world.isClient && this.ticksInSunlight < MATURATION_TIME) {
            this.ticksInSunlight += (20 * (60 + 120 * this.random.nextFloat()));
            boneMeal.decrement(1);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(this.getId());
            CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(CreeperSpores.CREEPERLING_FERTILIZATION_PACKET, buf);

            for (ServerPlayerEntity p : PlayerLookup.tracking(this)) {
                p.networkHandler.sendPacket(packet);
            }
        }
    }

    public static void createParticles(ThreadExecutor<?> ctx, PlayerEntity player, PacketByteBuf buf) {
        int entityId = buf.readInt();
        ctx.execute(() -> {
            Entity e = player.world.getEntityById(entityId);
            if (e instanceof CreeperlingEntity) {
                for(int i = 0; i < 15; ++i) {
                    Random random = e.world.random;
                    double speedX = random.nextGaussian() * 0.02D;
                    double speedY = random.nextGaussian() * 0.02D;
                    double speedZ = random.nextGaussian() * 0.02D;
                    e.world.addParticle(ParticleTypes.HAPPY_VILLAGER, e.getX() - 0.5 + random.nextFloat(), e.getY() + random.nextFloat(), e.getZ() - 0.5 + random.nextFloat(), speedX, speedY, speedZ);
                }
            }
        });
    }

    @Override
    public boolean damage(DamageSource cause, float amount) {
        if (super.damage(cause, amount)) {
            if (!world.isClient) {
                Entity attacker = cause.getAttacker();
                if (attacker instanceof OcelotEntity || attacker instanceof CatEntity) {
                    ((ServerWorld)this.world).spawnParticles(ParticleTypes.HEART, attacker.getX(), attacker.getY() + attacker.getStandingEyeHeight(), attacker.getZ(), 0, 0, 0.2f, 0, 0.1D);
                }
            }
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData data, @Nullable NbtCompound tag) {
        EntityData ret = super.initialize(world, difficulty, spawnReason, data, tag);
        float localDifficulty = difficulty.getClampedLocalDifficulty();
        this.ticksInSunlight = (int) (MATURATION_TIME * this.random.nextFloat() * 0.9 * localDifficulty);
        return ret;
    }

    @Override
    protected int getXpToDrop(PlayerEntity player) {
        return 2 + this.world.random.nextInt(3);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView worldView) {
        // Creeperlings like sunlight
        int skyLightLevel = worldView.getLightLevel(LightType.SKY, pos);
        // method_28516 == getBrightness
        float skyFavor = worldView.getDimension().getBrightness(skyLightLevel) * 3.0F;
        // But they can do with artificial light if there is not anything better
        // One day we will get in trouble for using this method, but in the mean time it's used by everything else
        @SuppressWarnings("deprecation") float brightnessAtPos = worldView.getBrightness(pos);
        float favor = Math.max(brightnessAtPos - 0.5F, skyFavor);
        // They like good soils too
        if (BlockTags.BAMBOO_PLANTABLE_ON.contains(worldView.getBlockState(pos.down(1)).getBlock())) {
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

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        if (this.isCharged()) {
            tag.putBoolean("powered", true);
        }
        tag.putBoolean("trusting", this.isTrusting());
        tag.putInt("ticksInSunlight", this.ticksInSunlight);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        if (tag.contains("powered")) {
            this.dataTracker.set(CHARGED, tag.getBoolean("powered"));
        }
        if (tag.contains("ticksInSunlight")) {
            this.ticksInSunlight = tag.getInt("ticksInSunlight");
        }
        if (tag.contains("trusting")) {
            this.setTrusting(tag.getBoolean("trusting"));
        }
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        super.onStruckByLightning(world, lightning);
        this.dataTracker.set(CHARGED, true);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient && this.world.getDifficulty() != Difficulty.PEACEFUL) {
            if (this.random.nextFloat() < this.getGrowthChance()) {
                ++this.ticksInSunlight;
            }
            if (this.ticksInSunlight >= MATURATION_TIME) {
                LivingEntity adult = kind.creeperType().create(world);
                if (adult == null) {    // fallback to vanilla creeper
                    adult = Objects.requireNonNull(CreeperEntry.getVanilla().creeperType().create(world));
                }

                EntityAttributeInstance adultMaxHealthAttr = adult.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                EntityAttributeInstance babyMaxHealthAttr = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);

                assert adultMaxHealthAttr != null && babyMaxHealthAttr != null;

                UUID adultUuid = adult.getUuid();
                double defaultMaxHealth = adultMaxHealthAttr.getBaseValue();
                double healthMultiplier = adultMaxHealthAttr.getValue() / babyMaxHealthAttr.getValue();
                adult.readNbt(this.writeNbt(new NbtCompound()));
                adult.setUuid(adultUuid);
                adultMaxHealthAttr.setBaseValue(defaultMaxHealth);
                adult.setHealth(adult.getHealth() * (float)healthMultiplier);

                world.spawnEntity(adult);
                pushOutOfBlocks(adult);
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private float getGrowthChance() {
        float skyExposition = this.world.getLightLevel(LightType.SKY, this.getBlockPos()) / 15f;
        return this.world.isDay() ? skyExposition : skyExposition * 0.5f * this.world.getMoonSize();
    }

    private static void pushOutOfBlocks(Entity self) {
        Box bb = self.getBoundingBox();
        EntityAccessor access = ((EntityAccessor) self);
        access.invokePushOutOfBlocks(self.getX() - (double)self.getWidth() * 0.35D, bb.minY + 0.5D, self.getZ() + (double)self.getWidth() * 0.35D);
        access.invokePushOutOfBlocks(self.getX() - (double)self.getWidth() * 0.35D, bb.minY + 0.5D, self.getZ() - (double)self.getWidth() * 0.35D);
        access.invokePushOutOfBlocks(self.getX() + (double)self.getWidth() * 0.35D, bb.minY + 0.5D, self.getZ() - (double)self.getWidth() * 0.35D);
        access.invokePushOutOfBlocks(self.getX() + (double)self.getWidth() * 0.35D, bb.minY + 0.5D, self.getZ() + (double)self.getWidth() * 0.35D);
    }

    @Override
    public boolean canImmediatelyDespawn(double sqDistance) {
        return sqDistance > (128*128);
    }

    @Override
    public float getSoundPitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource cause) {
        return SoundEvents.ENTITY_CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CREEPER_DEATH;
    }
}
