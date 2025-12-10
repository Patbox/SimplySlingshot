package eu.pb4.slingshot.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.block.SlingshotBlockTags;
import eu.pb4.slingshot.item.SlingshotDataComponentTags;
import eu.pb4.slingshot.item.SlingshotDataComponents;
import eu.pb4.slingshot.item.SlingshotItemTags;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentComponents;
import eu.pb4.slingshot.mixin.FireworkRocketEntityAccessor;
import eu.pb4.slingshot.mixin.LivingEntityAccessor;
import eu.pb4.slingshot.mixin.NoteBlockAccessor;
import eu.pb4.slingshot.util.BounceableExt;
import eu.pb4.slingshot.util.ServerWorldExt;
import eu.pb4.slingshot.util.TimedMiningProgress;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ItemProjectileEntity extends Projectile implements PolymerEntity, ItemSupplier, Bounceable {
    private ItemStack stack = ItemStack.EMPTY;
    private ItemStack weapon = ItemStack.EMPTY;
    private float roll = RandomSource.create().nextFloat() * Mth.TWO_PI;
    private boolean isReal = true;
    private boolean canModifyWorld = true;
    private boolean canPlaceBlock = false;
    private boolean canUseTools = false;
    private boolean returnOnEntityHit = false;
    private boolean returnOnBlockHit = false;
    private boolean allowSideEffects = true;
    private int returning = -1;

    public ItemProjectileEntity(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    public static ItemProjectileEntity create(Level world, Vec3 pos, ItemStack stack, @Nullable ItemStack weapon, @Nullable LivingEntity owner) {
        weapon = weapon != null ? weapon : ItemStack.EMPTY;

        var projectile = new ItemProjectileEntity(SlingshotEntities.ITEM_PROJECTILE, world);
        projectile.setPos(pos);
        projectile.setStack(stack.copyWithCount(1));
        projectile.setWeapon(weapon);
        projectile.setOwner(owner);

        if (stack.is(SlingshotItemTags.SLIME_LIKE) && projectile.allowSideEffects) {
            ((BounceableExt) projectile).slingshot$setBounces(3);
        }

        return projectile;
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        if (owner instanceof Player player) {
            this.canModifyWorld = player.mayBuild();
        }
    }

    public void tick() {
        if (this.level() instanceof ServerLevel world) {
            if (this.returning > 0) {
                this.returning--;
                int speedMult = 1;
                if (!this.isOwnerAlive()) {
                    this.tryDropSelf(world, Vec3.ZERO, false);
                    return;
                }
                var owner = this.getOwner();
                if (owner instanceof ServerPlayer player && this.position().distanceTo(owner.getEyePosition()) < owner.getBbWidth() + 0.25) {
                    player.connection.send(new ClientboundTakeItemEntityPacket(this.getId(), player.getId(), 1));
                    player.handleExtraItemsCreatedOnUse(this.stack);
                    this.discard();
                    return;
                }

                this.noPhysics = true;
                Vec3 vec3d = owner.getEyePosition().subtract(this.position());

                double d = 0.025 * (double) speedMult;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3d.normalize().scale(d)));
                this.setPos(new Vec3(this.getX(), this.getY() + vec3d.y * 0.015 * (double) speedMult, this.getZ()).add(this.getDeltaMovement()));
                this.updateRotation();
                super.tick();

                this.roll = (float) (this.roll - Math.min(this.getDeltaMovement().length(), Mth.PI * 3 / 4) / 2);
                this.entityData.set(EntityTrackedData.NO_GRAVITY, this.isNoGravity(), true);
                return;
            } else if (this.returning == 0) {
                this.tryDropSelf(world, Vec3.ZERO, false);
                return;
            }
        }

        this.tickInitialBubbleColumnCollision();
        this.applyGravity();
        this.applyDrag();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        Vec3 vec3d;
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getLocation();
        } else {
            vec3d = this.position().add(this.getDeltaMovement());
        }

        this.setPos(vec3d);
        this.updateRotation();
        this.applyEffectsFromBlocks();
        super.tick();
        if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
            try (var u = PolymerUtils.ignorePlaySoundExclusion()) {
                this.hitTargetOrDeflectSelf(hitResult);
            }
        }
        this.roll = (float) (this.roll + Math.min(this.getDeltaMovement().length(), Mth.PI * 3 / 4));
        this.entityData.set(EntityTrackedData.NO_GRAVITY, this.isNoGravity(), true);
    }


    @Override
    public void shoot(double x, double y, double z, float power, float uncertainty) {
        super.shoot(x, y, z, power, uncertainty);
        this.setYRot(-this.getYRot());
        this.setXRot(-this.getXRot());
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void updateRotation() {
        Vec3 vec3d = this.getDeltaMovement();
        double d = vec3d.horizontalDistance();
        this.setXRot(lerpRotation(this.xRotO, (float) -(Mth.atan2(vec3d.y, d) * 57.2957763671875)));
        this.setYRot(lerpRotation(this.yRotO, (float) -(Mth.atan2(vec3d.x, vec3d.z) * 57.2957763671875)));
    }


    @Override
    protected double getDefaultGravity() {
        return 0.06;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!(level() instanceof ServerLevel world)) {
            return;
        }
        super.onHitEntity(result);
        if (this.onGenericHit(world, result)) {
            return;
        }
        var entity = result.getEntity();

        if (SlingshotEvents.ITEM_PROJECTILE_ENTITY_HIT.invoker().onEntityHit(this, result)) {
            return;
        }

        if (this.allowSideEffects) {
            if (entity instanceof ArmorStand armorStandEntity && this.isReal) {
                var slot = armorStandEntity.getEquipmentSlotForItem(this.stack);
                if (armorStandEntity.canUseSlot(slot) && armorStandEntity.getItemBySlot(slot).isEmpty()) {
                    armorStandEntity.setItemSlot(slot, this.stack);
                    this.discard();
                    return;
                }
            }

            if (entity instanceof Animal animalEntity && animalEntity.isFood(this.stack) && this.getOwner() instanceof ServerPlayer player) {
                var tmp = ((LivingEntityAccessor) player).getEquipment().set(EquipmentSlot.OFFHAND, this.stack.copy());
                var s = animalEntity.mobInteract(player, InteractionHand.OFF_HAND);
                ((LivingEntityAccessor) player).getEquipment().set(EquipmentSlot.OFFHAND, tmp);
                if (!s.consumesAction()) {
                    this.tryDropSelf(world, Vec3.ZERO, this.returnOnEntityHit);
                } else {
                    world.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.stack), this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.2f);
                }
                return;
            }
            if (((this.canUseTools && this.stack.is(SlingshotItemTags.ENCHANTMENT_USABLE_ITEMS))
                    || this.stack.is(SlingshotItemTags.ALWAYS_ENTITY_USABLE_ITEMS)
                    || SlingshotDataComponentTags.contains(this.stack, SlingshotDataComponentTags.ALWAYS_ENTITY_USABLE_ITEMS)
            ) && this.isReal) {
                var fakePlayer = FakePlayer.get(world);
                fakePlayer.getInventory().clearContent();
                fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, this.stack);
                var action = fakePlayer.interactOn(entity, InteractionHand.MAIN_HAND);
                this.stack = fakePlayer.getItemBySlot(EquipmentSlot.MAINHAND);
                fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                fakePlayer.getInventory().forEach(stack1 -> this.spawnAtLocation(world, stack1));
                fakePlayer.getInventory().clearContent();
                if (action.consumesAction()) {
                    this.tryDropSelf(world, Vec3.ZERO, this.returnOnEntityHit);
                    return;
                }
            }
        }

        var baseDamage = this.weapon.getOrDefault(SlingshotDataComponents.SLINGSHOT_WEAPON_DAMAGE, 1.25f);
        var baseKnockback = this.weapon.getOrDefault(SlingshotDataComponents.SLINGSHOT_WEAPON_DAMAGE, 0f);
        double damage;
        double knockback = this.stack.getOrDefault(SlingshotDataComponents.SLINGSHOT_PROJECTILE_KNOCKBACK_BONUS, 0f) + baseKnockback;
        if (this.stack.isEnchanted() || !this.stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers().isEmpty()) {
            var attr = new AttributeMap(DefaultAttributes.getSupplier(EntityType.PLAYER));
            attr.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(0);
            this.stack.forEachModifier(EquipmentSlot.MAINHAND, ((entityAttributeRegistryEntry, entityAttributeModifier) -> {
                var x = attr.getInstance(entityAttributeRegistryEntry);
                if (x != null) x.addTransientModifier(entityAttributeModifier);
            }));

            damage = attr.getValue(Attributes.ATTACK_DAMAGE) / 1.5 * this.getDeltaMovement().length();
            knockback = attr.getValue(Attributes.ATTACK_KNOCKBACK) / 2 + baseKnockback;
        } else if (this.stack.is(SlingshotItemTags.HIGH_PROJECTILE_DAMAGE)) {
            damage = baseDamage * 3.6 * this.getDeltaMovement().length();
            knockback = 2 + baseKnockback;
        } else if (this.stack.is(SlingshotItemTags.MEDIUM_PROJECTILE_DAMAGE)) {
            damage = baseDamage * 1.8 * this.getDeltaMovement().length();
        } else if (this.stack.is(SlingshotItemTags.LOW_PROJECTILE_DAMAGE)) {
            damage = baseDamage * 0.4 * this.getDeltaMovement().length();
        } else {
            damage = baseDamage * this.getDeltaMovement().length();
        }
        var source = this.level().damageSources().mobProjectile(this, this.getOwner() instanceof LivingEntity owner ? owner : null);

        damage += this.stack.getOrDefault(SlingshotDataComponents.SLINGSHOT_PROJECTILE_DAMAGE_BONUS, 0f);
        damage = EnchantmentHelper.modifyDamage(world, this.weapon, entity, source, (float) damage);

        if (damage > 0) {
            entity.hurtServer(world, source, (float) damage);
        }

        if (this.stack.is(Items.PUFFERFISH) && entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, (int) (60 * 2 * this.getDeltaMovement().length()), 0), this);
            this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);
        }

        if (entity instanceof LivingEntity livingEntity && knockback > 0) {
            var dir = this.calculateHorizontalHurtKnockbackDirection(livingEntity, source);
            livingEntity.knockback(-knockback, dir.leftDouble(), dir.rightDouble());
        }

        var stackCopy = this.stack.copy();
        this.stack.hurtAndBreak(1, world, null, item -> {
            world.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stackCopy), this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.2f);
        });

        if (this.stack.is(Items.BLAZE_ROD) && this.allowSideEffects) {
            entity.igniteForSeconds(3);
            world.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.08f);
            this.discard();
            return;
        }

        this.tryDropSelf(world, Vec3.ZERO, this.returnOnEntityHit);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (!(level() instanceof ServerLevel world)) {
            this.discard();
            return;
        }

        var state = this.level().getBlockState(result.getBlockPos());
        var blockEntity = this.level().getBlockEntity(result.getBlockPos());
        if (state.isAir()) {
            this.tryDropSelf(world, Vec3.ZERO, this.returnOnBlockHit);
            return;
        }

        if (blockEntity instanceof JukeboxBlockEntity be && this.isReal && this.allowSideEffects) {
            if (!be.isEmpty()) {
                be.popOutTheItem();
            }
        }

        if (state.is(Blocks.NOTE_BLOCK)) {
            ((NoteBlockAccessor) state.getBlock()).callPlayNote(this, state, world, result.getBlockPos());
        }

        if (this.onGenericHit(world, result)) {
            return;
        }

        if (SlingshotEvents.ITEM_PROJECTILE_BLOCK_HIT.invoker().onBlockHit(this, result)) {
            return;
        }

        if (this.allowSideEffects) {
            if (this.canModifyWorld || this.stack.canPlaceOnBlockInAdventureMode(new BlockInWorld(world, result.getBlockPos(), false))) {
                if (this.stack.is(Items.BLAZE_ROD)) {
                    var fakePlayer = FakePlayer.get(world);
                    var itemUsage = new UseOnContext(this.level(), fakePlayer, InteractionHand.MAIN_HAND, Items.FIRE_CHARGE.getDefaultInstance(), result);
                    var action = Items.FIRE_CHARGE.useOn(itemUsage);
                    fakePlayer.getInventory().clearContent();
                    if (action.consumesAction()) {
                        world.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.08f);
                        this.discard();
                        return;
                    }
                }

                if (((this.canUseTools && this.stack.is(SlingshotItemTags.ENCHANTMENT_USABLE_ITEMS)) ||
                        (this.canPlaceBlock && this.stack.getItem() instanceof BlockItem)
                        || this.stack.is(SlingshotItemTags.ALWAYS_BLOCK_USABLE_ITEMS)
                        || SlingshotDataComponentTags.contains(this.stack, SlingshotDataComponentTags.ALWAYS_BLOCK_USABLE_ITEMS)
                ) && this.isReal) {
                    var fakePlayer = FakePlayer.get(world);
                    fakePlayer.getInventory().clearContent();
                    fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, this.stack);

                    var itemUsage = new UseOnContext(this.level(), fakePlayer, InteractionHand.MAIN_HAND, this.stack, result);
                    var action = this.stack.useOn(itemUsage);
                    if (!action.consumesAction()) {
                        action = state.useItemOn(stack, world, fakePlayer, InteractionHand.MAIN_HAND, result);
                        if (!action.consumesAction()) {
                            var abovePos = result.getBlockPos().relative(result.getDirection());
                            var aboveState = world.getBlockState(abovePos);
                            if (!aboveState.isAir() && aboveState.getCollisionShape(world, abovePos).isEmpty()) {
                                itemUsage = new UseOnContext(this.level(), fakePlayer, InteractionHand.MAIN_HAND, this.stack, result.withPosition(abovePos));
                                action = this.stack.useOn(itemUsage);
                                if (!action.consumesAction()) {
                                    action = state.useItemOn(stack, world, fakePlayer, InteractionHand.MAIN_HAND, result.withPosition(abovePos));
                                }
                            }
                        }
                    }

                    this.stack = fakePlayer.getItemBySlot(EquipmentSlot.MAINHAND);
                    fakePlayer.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    fakePlayer.getInventory().forEach(stack1 -> this.spawnAtLocation(world, stack1));
                    fakePlayer.getInventory().clearContent();
                    if (action.consumesAction()) {
                        this.tryDropSelf(world, result.getDirection().getUnitVec3().scale(0.2f), this.returnOnBlockHit);
                        return;
                    }
                }
            }

            if (this.canModifyWorld || this.stack.canBreakBlockInAdventureMode(new BlockInWorld(world, result.getBlockPos(), false))) {
                if (this.stack.is(SlingshotItemTags.BRICK_LIKE) && state.is(SlingshotBlockTags.BRICK_BREAKABLE)) {
                    world.destroyBlock(result.getBlockPos(), true);
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.6));

                    if (this.getDeltaMovement().lengthSqr() < 0.1) {
                        this.tryDropSelf(world, Vec3.ZERO, false);
                    }
                    return;
                }


                var tool = this.stack.get(DataComponents.TOOL);
                if (tool != null && tool.isCorrectForDrops(state)) {
                    var fake = FakePlayer.get(world);

                    var tmp = ((LivingEntityAccessor) fake).getEquipment().set(EquipmentSlot.MAINHAND, this.stack.copy());
                    var progress = ((ServerWorldExt) world).slingshot$getBreakingProgress(result.getBlockPos());

                    var speedMult = (float) Math.clamp(this.getDeltaMovement().lengthSqr(), 0.25, 5) * 8;

                    var delta = state.getDestroyProgress(fake, world, result.getBlockPos()) * speedMult;
                    if (progress != null) {
                        delta += progress.progress();
                        progress = new TimedMiningProgress(delta, this.level().getGameTime(), progress.entityId());
                    } else {
                        progress = new TimedMiningProgress(delta, this.level().getGameTime(), this.getId());
                    }

                    if (delta > 0 && delta < 1) {
                        world.destroyBlockProgress(progress.entityId(), result.getBlockPos(), (int) (delta * 9));
                        ((ServerWorldExt) world).slingshot$setBreakingProgress(result.getBlockPos(), progress);
                    }

                    if (delta > 1) {
                        world.destroyBlockProgress(progress.entityId(), result.getBlockPos(), -1);
                        ((ServerWorldExt) world).slingshot$setBreakingProgress(result.getBlockPos(), null);
                        fake.gameMode.destroyBlock(result.getBlockPos());
                    }

                    ((LivingEntityAccessor) fake).getEquipment().set(EquipmentSlot.MAINHAND, tmp);
                }
            }
        }

        this.tryDropSelf(world, result.getDirection().getUnitVec3().scale(0.2f), this.returnOnBlockHit);
    }

    @Override
    public void onBouncedOff(BlockHitResult result) {
        if (!(this.level() instanceof ServerLevel world)) return;

        var state = this.level().getBlockState(result.getBlockPos());
        if (state.is(Blocks.NOTE_BLOCK)) {
            ((NoteBlockAccessor) state.getBlock()).callPlayNote(this, state, this.level(), result.getBlockPos());
        }
        if (this.stack.is(Items.NOTE_BLOCK)) {
            var note = this.getRandom().nextInt(24);
            this.playSound(NoteBlockInstrument.HARP.getSoundEvent().value(), 2, NoteBlock.getPitchFromNote(note));
            world.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY(), this.getZ(), 0,(float) note / 24.0, 0.0, 0.0, 1f);
            world.gameEvent(this, GameEvent.NOTE_BLOCK_PLAY, this.blockPosition());
        }
    }

    public void tryDropSelf(ServerLevel world, Vec3 offset, boolean shouldReturn) {
        if (this.stack.isEmpty()) {
            this.discard();
            return;
        }

        if (SlingshotEvents.ITEM_PROJECTILE_TRY_DROP_SELF.invoker().onTryDropSelf(this, world, offset, shouldReturn)) {
            return;
        }

        if (this.stack.has(DataComponents.INTANGIBLE_PROJECTILE) || !this.isReal) {
            world.sendParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.08f);
            this.discard();
            return;
        }

        if (shouldReturn && this.isOwnerAlive()) {
            this.returning = 30 * 60;
            this.noPhysics = true;
            this.setNoGravity(true);
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            this.spawnAtLocation(world, this.stack, offset);
            this.discard();
        }
    }

    protected boolean onGenericHit(ServerLevel world, HitResult result) {
        if (this.stack.is(Items.NOTE_BLOCK)) {
            var note = this.getRandom().nextInt(24);
            this.playSound(NoteBlockInstrument.HARP.getSoundEvent().value(), 2, NoteBlock.getPitchFromNote(note));
            world.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY(), this.getZ(), 0,(float) note / 24.0, 0.0, 0.0, 1f);
            world.gameEvent(this, GameEvent.NOTE_BLOCK_PLAY, this.blockPosition());
        }

        if (SlingshotEvents.ITEM_PROJECTILE_GENERIC_HIT.invoker().onGenericHit(this, result)) {
            return true;
        }

        if (this.stack.is(Items.FIREWORK_STAR) && this.allowSideEffects) {
            var fireworkStack = Items.FIREWORK_ROCKET.getDefaultInstance();
            fireworkStack.set(DataComponents.ITEM_MODEL, Items.AIR.components().get(DataComponents.ITEM_MODEL));
            fireworkStack.set(DataComponents.FIREWORKS, new Fireworks(0,
                    List.of(this.stack.getOrDefault(DataComponents.FIREWORK_EXPLOSION, FireworkExplosion.DEFAULT))));
            var firework = new FireworkRocketEntity(world, result.getLocation().x(), result.getLocation().y(), result.getLocation().z(), fireworkStack);
            firework.setOwner(this.getOwner());
            world.addFreshEntity(firework);
            ((FireworkRocketEntityAccessor) firework).callExplode(world);
            this.discard();
            return true;
        }

        if (this.stack.getItem() instanceof SpawnEggItem spawnEggItem && this.allowSideEffects) {
            //noinspection unchecked
            var entityType = (EntityType<Entity>) spawnEggItem.getType(this.stack);
            var callback = EntityType.appendDefaultStackConfig(entity -> {
                entity.setPos(result.getLocation());
            }, this.level(), this.stack, this.getOwner() instanceof LivingEntity owner ? owner : null);
            var spawned = entityType.spawn(world, callback, BlockPos.containing(result.getLocation()), EntitySpawnReason.SPAWN_ITEM_USE, false, false);
            if (spawned != null) {
                this.level().gameEvent(this, GameEvent.ENTITY_PLACE, BlockPos.containing(result.getLocation()));
                this.discard();
                return true;
            }
        }

        if (this.stack.is(Items.BREEZE_ROD) && this.allowSideEffects) {

            world.explode(this, null,
                    new SimpleExplosionDamageCalculator(true, false, Optional.of(1.5f),
                            BuiltInRegistries.BLOCK.get(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())),
                    this.getX(), this.getY(), this.getZ(), 2, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE,
                    WeightedList.of(), SoundEvents.WIND_CHARGE_BURST);
            this.discard();
            return true;
        }

        return false;
    }

    private void tickInitialBubbleColumnCollision() {
        if (this.firstTick) {
            Iterator var1 = BlockPos.betweenClosed(this.getBoundingBox()).iterator();

            while (var1.hasNext()) {
                BlockPos blockPos = (BlockPos) var1.next();
                BlockState blockState = this.level().getBlockState(blockPos);
                if (blockState.is(Blocks.BUBBLE_COLUMN)) {
                    blockState.entityInside(this.level(), blockPos, this, InsideBlockEffectApplier.NOOP, true);
                }
            }
        }
    }

    private void applyDrag() {
        Vec3 vec3d = this.getDeltaMovement();
        Vec3 vec3d2 = this.position();
        float g;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f = 0.25F;
                if (this.level() instanceof ServerLevel serverWorld) {
                    serverWorld.sendParticles(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, 0, vec3d.x, vec3d.y, vec3d.z, 1);
                }
            }

            g = 0.8F;
        } else {
            g = 0.99F;
        }

        this.setDeltaMovement(vec3d.scale(g));
    }

    private void setWeapon(ItemStack stack) {
        this.weapon = stack;
        this.canPlaceBlock = EnchantmentHelper.has(weapon, SlingshotEnchantmentComponents.PROJECTILE_BLOCK_PLACER);
        this.canUseTools = EnchantmentHelper.has(weapon, SlingshotEnchantmentComponents.PROJECTILE_TOOL_USER);
        this.returnOnEntityHit = EnchantmentHelper.has(weapon, SlingshotEnchantmentComponents.PROJECTILE_BOOMERANG_ENTITY);
        this.returnOnBlockHit = EnchantmentHelper.has(weapon, SlingshotEnchantmentComponents.PROJECTILE_BOOMERANG_BLOCK);
        this.allowSideEffects = !EnchantmentHelper.has(weapon, SlingshotEnchantmentComponents.PROJECTILE_ITEM_NO_SIDE_EFFECTS);
    }

    @Nullable
    @Override
    public ItemStack getWeaponItem() {
        return this.weapon;
    }

    public ItemStack getItem() {
        return this.stack;
    }

    private void setStack(ItemStack itemStack) {
        this.stack = itemStack;
    }

    public boolean allowSideEffects() {
        return allowSideEffects;
    }

    public boolean canModifyWorld() {
        return canModifyWorld;
    }

    public boolean canPlaceBlock() {
        return canPlaceBlock;
    }

    public boolean canUseTools() {
        return canUseTools;
    }

    public boolean returnOnBlockHit() {
        return returnOnBlockHit;
    }

    public boolean returnOnEntityHit() {
        return returnOnEntityHit;
    }

    public boolean isReal() {
        return isReal;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.store("stack", ItemStack.OPTIONAL_CODEC, this.stack);
        view.store("weapon", ItemStack.OPTIONAL_CODEC, this.weapon);
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
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.stack = view.read("stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.weapon = view.read("weapon", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.isReal = view.getBooleanOr("is_real", true);
        this.canModifyWorld = view.getBooleanOr("can_modify_world", true);
        this.canPlaceBlock = view.getBooleanOr("can_place_blocks", false);
        this.canUseTools = view.getBooleanOr("can_use_tools", false);
        this.returnOnBlockHit = view.getBooleanOr("return_on_block_hit", false);
        this.returnOnEntityHit = view.getBooleanOr("return_on_entity_hit", false);
        this.allowSideEffects = view.getBooleanOr("allow_side_effects", true);
        this.returning = view.getIntOr("returning", -1);
        this.noPhysics = this.returning != -1;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        if (initial) {
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.TELEPORTATION_DURATION, 2));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.INTERPOLATION_DURATION, 2));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.Item.ITEM, this.stack));
        } else {
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.START_INTERPOLATION, 0));
        }

        var rotation = new Quaternionf();

        if (this.allowSideEffects && this.stack.is(SlingshotItemTags.ROTATE_ON_Y_AXIS)) {
            rotation.rotateX(Mth.HALF_PI);
            rotation.rotateY(this.roll);
            if (this.stack.is(SlingshotItemTags.ROTATE_ON_Y_AXIS_N45_DEG)) {
                rotation.rotateZ(Mth.HALF_PI / 2);
            } else if (this.stack.is(SlingshotItemTags.ROTATE_ON_Y_AXIS_45_DEG)) {
                rotation.rotateZ(-Mth.HALF_PI / 2);
            } else if (this.stack.is(SlingshotItemTags.ROTATE_ON_Y_AXIS_180_DEG)) {
                rotation.rotateZ(Mth.PI);
            }
        } else {
            rotation.rotateX(this.roll);
        }

        data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.LEFT_ROTATION, rotation));
        data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.SCALE, new Vector3f(this.returning == -1 ? 0.55f : 0.35f)));
    }

    @Override
    public Vec3 trackingPosition() {
        return super.trackingPosition().add(0, this.getBbHeight() / 2, 0);
    }

    public void setReal(boolean b) {
        this.isReal = b;
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayer) || !entity.isSpectator();
        } else {
            return false;
        }
    }
}
