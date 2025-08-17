package eu.pb4.slingshot.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import eu.pb4.slingshot.block.SlingshotBlockTags;
import eu.pb4.slingshot.item.SlingshotItemTags;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentComponents;
import eu.pb4.slingshot.mixin.LivingEntityAccessor;
import eu.pb4.slingshot.mixin.NoteBlockAccessor;
import eu.pb4.slingshot.util.BounceableExt;
import eu.pb4.slingshot.util.ServerWorldExt;
import eu.pb4.slingshot.util.TimedMiningProgress;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ItemProjectileEntity extends ProjectileEntity implements PolymerEntity, FlyingItemEntity, Bounceable {
    private ItemStack stack = ItemStack.EMPTY;
    private ItemStack weapon = ItemStack.EMPTY;
    private float roll = Random.create().nextFloat() * MathHelper.TAU;
    private boolean isReal = true;
    private boolean canModifyWorld = true;
    private boolean canPlaceBlock = false;
    private boolean canUseTools = false;
    private boolean returnOnEntityHit = false;
    private boolean returnOnBlockHit = false;
    private boolean allowSideEffects = true;
    private int returning = -1;

    public ItemProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static ItemProjectileEntity create(World world, Vec3d pos, ItemStack stack, @Nullable ItemStack weapon, @Nullable LivingEntity owner) {
        weapon = weapon != null ? weapon : ItemStack.EMPTY;

        var projectile = new ItemProjectileEntity(SlingshotEntities.ITEM_PROJECTILE, world);
        projectile.setPosition(pos);
        projectile.setStack(stack.copyWithCount(1));
        projectile.setWeapon(weapon);
        projectile.setOwner(owner);

        if (stack.isIn(SlingshotItemTags.SLIME_LIKE) && projectile.allowSideEffects) {
            ((BounceableExt) projectile).slingshot$setBounces(3);
        }

        return projectile;
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        if (owner instanceof PlayerEntity player) {
            this.canModifyWorld = player.canModifyBlocks();
        }
    }

    public void tick() {
        if (this.getWorld() instanceof ServerWorld world) {
            if (this.returning > 0) {
                this.returning--;
                int speedMult = 1;
                if (!this.isOwnerAlive()) {
                    this.tryDropSelf(world, Vec3d.ZERO, false);
                    return;
                }
                var owner = this.getOwner();
                if (owner instanceof ServerPlayerEntity player && this.getPos().distanceTo(owner.getEyePos()) < owner.getWidth() + 0.25) {
                    player.networkHandler.sendPacket(new ItemPickupAnimationS2CPacket(this.getId(), player.getId(), 1));
                    player.giveOrDropStack(this.stack);
                    this.discard();
                    return;
                }

                this.noClip = true;
                Vec3d vec3d = owner.getEyePos().subtract(this.getPos());

                double d = 0.025 * (double) speedMult;
                this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                this.setPosition(new Vec3d(this.getX(), this.getY() + vec3d.y * 0.015 * (double) speedMult, this.getZ()).add(this.getVelocity()));
                this.updateRotation();
                super.tick();

                this.roll = (float) (this.roll - Math.min(this.getVelocity().length(), MathHelper.PI * 3 / 4) / 2);
                this.dataTracker.set(EntityTrackedData.NO_GRAVITY, this.hasNoGravity(), true);
                return;
            } else if (this.returning == 0) {
                this.tryDropSelf(world, Vec3d.ZERO, false);
                return;
            }
        }

        this.tickInitialBubbleColumnCollision();
        this.applyGravity();
        this.applyDrag();
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        Vec3d vec3d;
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getPos();
        } else {
            vec3d = this.getPos().add(this.getVelocity());
        }

        this.setPosition(vec3d);
        this.updateRotation();
        this.tickBlockCollision();
        super.tick();
        if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
            try (var u = PolymerUtils.ignorePlaySoundExclusion()) {
                this.hitOrDeflect(hitResult);
            }
        }
        this.roll = (float) (this.roll + Math.min(this.getVelocity().length(), MathHelper.PI * 3 / 4));
        this.dataTracker.set(EntityTrackedData.NO_GRAVITY, this.hasNoGravity(), true);
    }


    @Override
    public void setVelocity(double x, double y, double z, float power, float uncertainty) {
        super.setVelocity(x, y, z, power, uncertainty);
        this.setYaw(-this.getYaw());
        this.setPitch(-this.getPitch());
        this.lastYaw = this.getYaw();
        this.lastPitch = this.getPitch();
    }

    @Override
    protected void updateRotation() {
        Vec3d vec3d = this.getVelocity();
        double d = vec3d.horizontalLength();
        this.setPitch(updateRotation(this.lastPitch, (float) -(MathHelper.atan2(vec3d.y, d) * 57.2957763671875)));
        this.setYaw(updateRotation(this.lastYaw, (float) -(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875)));
    }


    @Override
    protected double getGravity() {
        return 0.06;
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {
        if (!(getWorld() instanceof ServerWorld world)) {
            return;
        }
        super.onEntityHit(result);
        if (this.onGenericHit(world, result)) {
            this.discard();
            return;
        }
        var entity = result.getEntity();

        if (this.allowSideEffects) {
            if (entity instanceof ArmorStandEntity armorStandEntity && this.isReal) {
                var slot = armorStandEntity.getPreferredEquipmentSlot(this.stack);
                if (armorStandEntity.canUseSlot(slot) && armorStandEntity.getEquippedStack(slot).isEmpty()) {
                    armorStandEntity.equipStack(slot, this.stack);
                    this.discard();
                    return;
                }
            }

            if (entity instanceof AnimalEntity animalEntity && animalEntity.isBreedingItem(this.stack) && this.getOwner() instanceof ServerPlayerEntity player) {
                var tmp = ((LivingEntityAccessor) player).getEquipment().put(EquipmentSlot.OFFHAND, this.stack.copy());
                var s = animalEntity.interactMob(player, Hand.OFF_HAND);
                ((LivingEntityAccessor) player).getEquipment().put(EquipmentSlot.OFFHAND, tmp);
                if (!s.isAccepted()) {
                    this.tryDropSelf(world, Vec3d.ZERO, this.returnOnEntityHit);
                } else {
                    world.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.stack), this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.2f);
                }
                return;
            }
            if (((this.canUseTools && this.stack.isIn(SlingshotItemTags.ENCHANTMENT_USABLE_ITEMS))
                    || this.stack.isIn(SlingshotItemTags.ALWAYS_ENTITY_USABLE_ITEMS)) && this.isReal) {
                var fakePlayer = FakePlayer.get(world);
                fakePlayer.getInventory().clear();
                fakePlayer.equipStack(EquipmentSlot.MAINHAND, this.stack);
                var action = fakePlayer.interact(entity, Hand.MAIN_HAND);
                this.stack = fakePlayer.getEquippedStack(EquipmentSlot.MAINHAND);
                fakePlayer.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                fakePlayer.getInventory().forEach(stack1 -> this.dropStack(world, stack1));
                fakePlayer.getInventory().clear();
                if (action.isAccepted()) {
                    this.tryDropSelf(world, Vec3d.ZERO, this.returnOnEntityHit);
                    return;
                }
            }
        }

        double damage;
        double knockback = 0d;
        if (this.stack.hasEnchantments() || !this.stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).modifiers().isEmpty()) {
            var attr = new AttributeContainer(DefaultAttributeRegistry.get(EntityType.PLAYER));
            attr.getCustomInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(0);
            this.stack.applyAttributeModifiers(EquipmentSlot.MAINHAND, ((entityAttributeRegistryEntry, entityAttributeModifier) -> {
                var x = attr.getCustomInstance(entityAttributeRegistryEntry);
                if (x != null) x.addTemporaryModifier(entityAttributeModifier);
            }));

            damage = attr.getValue(EntityAttributes.ATTACK_DAMAGE) / 1.5 * this.getVelocity().length();
            knockback = attr.getValue(EntityAttributes.ATTACK_KNOCKBACK) / 2;
        } else if (this.stack.isIn(SlingshotItemTags.HIGH_PROJECTILE_DAMAGE)) {
            damage = 4.5 * this.getVelocity().length();
            knockback = 2;
        } else if (this.stack.isIn(SlingshotItemTags.MEDIUM_PROJECTILE_DAMAGE)) {
            damage = 2.25 * this.getVelocity().length();
        } else if (this.stack.isIn(SlingshotItemTags.LOW_PROJECTILE_DAMAGE)) {
            damage = 0.5 * this.getVelocity().length();
        } else {
            damage = 1.25 * this.getVelocity().length();
        }
        var source = this.getWorld().getDamageSources().mobProjectile(this, this.getOwner() instanceof LivingEntity owner ? owner : null);

        damage = EnchantmentHelper.getDamage(world, this.weapon, entity, source, (float) damage);

        if (damage > 0) {
            entity.damage(world, source, (float) damage);
        }

        if (this.stack.isOf(Items.PUFFERFISH) && entity instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, (int) (60 * 2 * this.getVelocity().length()), 0), this);
            this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
        }

        if (entity instanceof LivingEntity livingEntity && knockback > 0) {
            var dir = this.getKnockback(livingEntity, source);
            livingEntity.takeKnockback(knockback, dir.leftDouble(), dir.rightDouble());
        }

        var stackCopy = this.stack.copy();
        this.stack.damage(1, world, null, item -> {
            world.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stackCopy), this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.2f);
        });

        if (this.stack.isOf(Items.BLAZE_ROD) && this.allowSideEffects) {
            entity.setOnFireFor(3);
            world.spawnParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.08f);
            this.discard();
            return;
        }

        this.tryDropSelf(world, Vec3d.ZERO, this.returnOnEntityHit);
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);

        if (!(getWorld() instanceof ServerWorld world)) {
            this.discard();
            return;
        }

        var state = this.getWorld().getBlockState(result.getBlockPos());
        var blockEntity = this.getWorld().getBlockEntity(result.getBlockPos());
        if (state.isAir()) {
            this.tryDropSelf(world, Vec3d.ZERO, this.returnOnBlockHit);
            return;
        }

        if (blockEntity instanceof JukeboxBlockEntity be && this.isReal && this.allowSideEffects) {
            if (!be.isEmpty()) {
                be.dropRecord();
            }
        }

        if (state.isOf(Blocks.NOTE_BLOCK)) {
            ((NoteBlockAccessor) state.getBlock()).callPlayNote(this, state, world, result.getBlockPos());
        }

        if (this.onGenericHit(world, result)) {
            this.discard();
            return;
        }

        if (this.allowSideEffects) {
            if (this.canModifyWorld || this.stack.canPlaceOn(new CachedBlockPosition(world, result.getBlockPos(), false))) {
                if (this.stack.isOf(Items.BLAZE_ROD)) {
                    var fakePlayer = FakePlayer.get(world);
                    var itemUsage = new ItemUsageContext(this.getWorld(), fakePlayer, Hand.MAIN_HAND, Items.FIRE_CHARGE.getDefaultStack(), result);
                    var action = Items.FIRE_CHARGE.useOnBlock(itemUsage);
                    fakePlayer.getInventory().clear();
                    if (action.isAccepted()) {
                        world.spawnParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.08f);
                        this.discard();
                        return;
                    }
                }

                if (((this.canUseTools && this.stack.isIn(SlingshotItemTags.ENCHANTMENT_USABLE_ITEMS)) ||
                        (this.canPlaceBlock && this.stack.getItem() instanceof BlockItem)
                        || this.stack.isIn(SlingshotItemTags.ALWAYS_BLOCK_USABLE_ITEMS)) && this.isReal) {
                    var fakePlayer = FakePlayer.get(world);
                    fakePlayer.getInventory().clear();
                    fakePlayer.equipStack(EquipmentSlot.MAINHAND, this.stack);

                    var itemUsage = new ItemUsageContext(this.getWorld(), fakePlayer, Hand.MAIN_HAND, this.stack, result);
                    var action = this.stack.useOnBlock(itemUsage);
                    if (!action.isAccepted()) {
                        action = state.onUseWithItem(stack, world, fakePlayer, Hand.MAIN_HAND, result);
                        if (!action.isAccepted()) {
                            var abovePos = result.getBlockPos().offset(result.getSide());
                            var aboveState = world.getBlockState(abovePos);
                            if (!aboveState.isAir() && aboveState.getCollisionShape(world, abovePos).isEmpty()) {
                                itemUsage = new ItemUsageContext(this.getWorld(), fakePlayer, Hand.MAIN_HAND, this.stack, result.withBlockPos(abovePos));
                                action = this.stack.useOnBlock(itemUsage);
                                if (!action.isAccepted()) {
                                    action = state.onUseWithItem(stack, world, fakePlayer, Hand.MAIN_HAND, result.withBlockPos(abovePos));
                                }
                            }
                        }
                    }

                    this.stack = fakePlayer.getEquippedStack(EquipmentSlot.MAINHAND);
                    fakePlayer.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    fakePlayer.getInventory().forEach(stack1 -> this.dropStack(world, stack1));
                    fakePlayer.getInventory().clear();
                    if (action.isAccepted()) {
                        this.tryDropSelf(world, result.getSide().getDoubleVector().multiply(0.2f), this.returnOnBlockHit);
                        return;
                    }
                }
            }

            if (this.canModifyWorld || this.stack.canBreak(new CachedBlockPosition(world, result.getBlockPos(), false))) {
                if (this.stack.isIn(SlingshotItemTags.BRICK_LIKE) && state.isIn(SlingshotBlockTags.BRICK_BREAKABLE)) {
                    world.breakBlock(result.getBlockPos(), true);
                    this.setVelocity(this.getVelocity().multiply(0.6));

                    if (this.getVelocity().lengthSquared() < 0.1) {
                        this.tryDropSelf(world, Vec3d.ZERO, false);
                    }
                    return;
                }


                var tool = this.stack.get(DataComponentTypes.TOOL);
                if (tool != null && tool.isCorrectForDrops(state)) {
                    var fake = FakePlayer.get(world);

                    var tmp = ((LivingEntityAccessor) fake).getEquipment().put(EquipmentSlot.MAINHAND, this.stack.copy());
                    var progress = ((ServerWorldExt) world).slingshot$getBreakingProgress(result.getBlockPos());

                    var speedMult = (float) Math.clamp(this.getVelocity().lengthSquared(), 0.25, 5) * 8;

                    var delta = state.calcBlockBreakingDelta(fake, world, result.getBlockPos()) * speedMult;
                    if (progress != null) {
                        delta += progress.progress();
                        progress = new TimedMiningProgress(delta, this.getWorld().getTime(), progress.entityId());
                    } else {
                        progress = new TimedMiningProgress(delta, this.getWorld().getTime(), this.getId());
                    }

                    if (delta > 0 && delta < 1) {
                        world.setBlockBreakingInfo(progress.entityId(), result.getBlockPos(), (int) (delta * 9));
                        ((ServerWorldExt) world).slingshot$setBreakingProgress(result.getBlockPos(), progress);
                    }

                    if (delta > 1) {
                        world.setBlockBreakingInfo(progress.entityId(), result.getBlockPos(), -1);
                        ((ServerWorldExt) world).slingshot$setBreakingProgress(result.getBlockPos(), null);
                        fake.interactionManager.tryBreakBlock(result.getBlockPos());
                    }

                    ((LivingEntityAccessor) fake).getEquipment().put(EquipmentSlot.MAINHAND, tmp);
                }
            }
        }

        this.tryDropSelf(world, result.getSide().getDoubleVector().multiply(0.2f), this.returnOnBlockHit);
    }

    @Override
    public void onBouncedOff(BlockHitResult result) {
        if (!(this.getWorld() instanceof ServerWorld world)) return;

        var state = this.getWorld().getBlockState(result.getBlockPos());
        if (state.isOf(Blocks.NOTE_BLOCK)) {
            ((NoteBlockAccessor) state.getBlock()).callPlayNote(this, state, this.getWorld(), result.getBlockPos());
        }
        if (this.stack.isOf(Items.NOTE_BLOCK)) {
            var note = this.getRandom().nextInt(24);
            this.playSound(NoteBlockInstrument.HARP.getSound().value(), 2, NoteBlock.getNotePitch(note));
            world.spawnParticles(ParticleTypes.NOTE, this.getX(), this.getY(), this.getZ(), 0,(float) note / 24.0, 0.0, 0.0, 1f);
            world.emitGameEvent(this, GameEvent.NOTE_BLOCK_PLAY, this.getBlockPos());
        }
    }

    private void tryDropSelf(ServerWorld world, Vec3d offset, boolean shouldReturn) {
        if (this.stack.isEmpty()) {
            this.discard();
            return;
        }

        if (this.stack.contains(DataComponentTypes.INTANGIBLE_PROJECTILE) || !this.isReal) {
            world.spawnParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.08f);
            this.discard();
            return;
        }

        if (shouldReturn && this.isOwnerAlive()) {
            this.returning = 30 * 60;
            this.noClip = true;
            this.setNoGravity(true);
            this.setVelocity(Vec3d.ZERO);
        } else {
            this.dropStack(world, this.stack, offset);
            this.discard();
        }
    }

    protected boolean onGenericHit(ServerWorld world, HitResult result) {
        if (this.stack.isOf(Items.NOTE_BLOCK)) {
            var note = this.getRandom().nextInt(24);
            this.playSound(NoteBlockInstrument.HARP.getSound().value(), 2, NoteBlock.getNotePitch(note));
            world.spawnParticles(ParticleTypes.NOTE, this.getX(), this.getY(), this.getZ(), 0,(float) note / 24.0, 0.0, 0.0, 1f);
            world.emitGameEvent(this, GameEvent.NOTE_BLOCK_PLAY, this.getBlockPos());
        }

        if (this.stack.getItem() instanceof SpawnEggItem spawnEggItem && this.allowSideEffects) {
            //noinspection unchecked
            var entityType = (EntityType<Entity>) spawnEggItem.getEntityType(this.getWorld().getRegistryManager(), this.stack);
            var callback = EntityType.copier(entity -> {
                entity.setPosition(result.getPos());
            }, this.getWorld(), this.stack, this.getOwner() instanceof LivingEntity owner ? owner : null);
            var spawned = entityType.spawn(world, callback, BlockPos.ofFloored(result.getPos()), SpawnReason.SPAWN_ITEM_USE, false, false);
            if (spawned != null) {
                this.getWorld().emitGameEvent(this, GameEvent.ENTITY_PLACE, BlockPos.ofFloored(result.getPos()));
                return true;
            }
        }

        if (this.stack.isOf(Items.BREEZE_ROD) && this.allowSideEffects) {
            world.createExplosion(this, null,
                    new AdvancedExplosionBehavior(true, false, Optional.of(1.5f),
                            Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())),
                            this.getX(), this.getY(), this.getZ(), 2, false, World.ExplosionSourceType.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST);
            return true;
        }
        return false;
    }

    private void tickInitialBubbleColumnCollision() {
        if (this.firstUpdate) {
            Iterator var1 = BlockPos.iterate(this.getBoundingBox()).iterator();

            while (var1.hasNext()) {
                BlockPos blockPos = (BlockPos) var1.next();
                BlockState blockState = this.getWorld().getBlockState(blockPos);
                if (blockState.isOf(Blocks.BUBBLE_COLUMN)) {
                    blockState.onEntityCollision(this.getWorld(), blockPos, this, EntityCollisionHandler.DUMMY);
                }
            }
        }
    }

    private void applyDrag() {
        Vec3d vec3d = this.getVelocity();
        Vec3d vec3d2 = this.getPos();
        float g;
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float f = 0.25F;
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, 0, vec3d.x, vec3d.y, vec3d.z, 1);
                }
            }

            g = 0.8F;
        } else {
            g = 0.99F;
        }

        this.setVelocity(vec3d.multiply(g));
    }

    private void setWeapon(ItemStack stack) {
        this.weapon = stack;
        this.canPlaceBlock = EnchantmentHelper.hasAnyEnchantmentsWith(weapon, SlingshotEnchantmentComponents.PROJECTILE_BLOCK_PLACER);
        this.canUseTools = EnchantmentHelper.hasAnyEnchantmentsWith(weapon, SlingshotEnchantmentComponents.PROJECTILE_TOOL_USER);
        this.returnOnEntityHit = EnchantmentHelper.hasAnyEnchantmentsWith(weapon, SlingshotEnchantmentComponents.PROJECTILE_BOOMERANG_ENTITY);
        this.returnOnBlockHit = EnchantmentHelper.hasAnyEnchantmentsWith(weapon, SlingshotEnchantmentComponents.PROJECTILE_BOOMERANG_BLOCK);
        this.allowSideEffects = !EnchantmentHelper.hasAnyEnchantmentsWith(weapon, SlingshotEnchantmentComponents.PROJECTILE_ITEM_NO_SIDE_EFFECTS);
    }

    @Nullable
    @Override
    public ItemStack getWeaponStack() {
        return this.weapon;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    private void setStack(ItemStack itemStack) {
        this.stack = itemStack;
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put("stack", ItemStack.OPTIONAL_CODEC, this.stack);
        view.put("weapon", ItemStack.OPTIONAL_CODEC, this.weapon);
        view.putBoolean("is_real", this.isReal);
        view.putBoolean("can_modify_world", this.canModifyWorld);
        view.putBoolean("can_place_blocks", this.canPlaceBlock);
        view.putBoolean("can_use_tools", this.canUseTools);
        view.putBoolean("return_on_block_hit", this.returnOnBlockHit);
        view.putBoolean("return_on_entity_hit", this.returnOnEntityHit);
        view.putBoolean("allow_side_effects", this.allowSideEffects);
        if (this.returning != -1) view.putInt("returning", this.returning);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.stack = view.read("stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.weapon = view.read("weapon", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.isReal = view.getBoolean("is_real", true);
        this.canModifyWorld = view.getBoolean("can_modify_world", true);
        this.canPlaceBlock = view.getBoolean("can_place_blocks", false);
        this.canUseTools = view.getBoolean("can_use_tools", false);
        this.returnOnBlockHit = view.getBoolean("return_on_block_hit", false);
        this.returnOnEntityHit = view.getBoolean("return_on_entity_hit", false);
        this.allowSideEffects = view.getBoolean("allow_side_effects", true);
        this.returning = view.getInt("returning", -1);
        this.noClip = this.returning != -1;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        if (initial) {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TELEPORTATION_DURATION, 2));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.INTERPOLATION_DURATION, 2));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.Item.ITEM, this.stack));
        } else {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.START_INTERPOLATION, 0));
        }

        var rotation = new Quaternionf();

        if (this.stack.isIn(SlingshotItemTags.ROTATE_LIKE_ITEM_ROD) && this.allowSideEffects) {
            rotation.rotateX(MathHelper.HALF_PI);
            rotation.rotateY(this.roll);
            rotation.rotateZ(-MathHelper.HALF_PI / 2);
        } else if (this.stack.isIn(SlingshotItemTags.ROTATE_LIKE_BLOCK_ROD) && this.allowSideEffects) {
            rotation.rotateX(MathHelper.HALF_PI);
            rotation.rotateY(this.roll);
        } else {
            rotation.rotateX(this.roll);
        }

        data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.LEFT_ROTATION, rotation));
        data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.SCALE, new Vector3f(this.returning == -1 ? 0.55f : 0.35f)));
    }

    @Override
    public Vec3d getSyncedPos() {
        return super.getSyncedPos().add(0, this.getHeight() / 2, 0);
    }

    public void setReal(boolean b) {
        this.isReal = b;
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }
}
