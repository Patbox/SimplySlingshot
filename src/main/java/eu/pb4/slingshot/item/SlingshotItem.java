package eu.pb4.slingshot.item;

import com.google.common.base.Predicates;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.slingshot.ModInit;
import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.entity.FakeProjectileEntity;
import eu.pb4.slingshot.entity.ItemProjectileEntity;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentComponents;
import eu.pb4.slingshot.item.ench.SlingshotEnchantments;
import eu.pb4.slingshot.util.MirrorLevel;
import eu.pb4.slingshot.util.NetHandlerExt;
import eu.pb4.slingshot.util.SlingshotSoundEvents;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static eu.pb4.slingshot.ModInit.id;

public class SlingshotItem extends ProjectileWeaponItem implements PolymerItem {
    private static final Style HOTBAR_OVERLAY_STYLE = Style.EMPTY.withFont(new FontDescription.Resource(id("hotbar_overlay"))).withShadowColor(0);

    public SlingshotItem(Properties settings) {
        super(settings);
    }

    public static float getPullProgress(int useTicks, ItemStack stack, LivingEntity user) {
        var f = useTicks / 20.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        f /= EnchantmentHelper.modifyCrossbowChargingTime(stack, user, 1);

        if (f > 1.0f) {
            f = 1.0f;
        }

        return f;
    }

    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        var hand = user.getItemInHand(InteractionHand.MAIN_HAND) == stack ? InteractionHand.MAIN_HAND : user.getItemInHand(InteractionHand.OFF_HAND) == stack ? InteractionHand.OFF_HAND : null;
        if (!(user instanceof Player playerEntity)) {
            return false;
        } else {
            var itemStack = getProjectileTypeSource(playerEntity, stack, user.getUsedItemHand());
            if (itemStack.isEmpty()) {
                syncHand(user, hand);
                return false;
            } else {
                var useTime = this.getUseDuration(stack, user) - remainingUseTicks;
                float progress = getPullProgress(useTime, stack, user);
                if (progress < 0.3) {
                    syncHand(user, hand);
                    return false;
                } else {
                    var list = this.getProjectilesFrom(stack, itemStack.stack(), playerEntity, true);
                    if (world instanceof ServerLevel serverWorld) {
                        if (!list.isEmpty()) {
                            this.shoot(serverWorld, playerEntity, playerEntity.getUsedItemHand(), stack, list, getSpeed(stack, serverWorld, user, progress), 1.0F, false, null);
                        }
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SlingshotSoundEvents.ENTITY_SLINGSHOT_SHOOT, SoundSource.PLAYERS, 1.0F, 1 / (world.getRandom().nextFloat() * 0.4F + 1.2F) + progress * 0.5F);
                    playerEntity.awardStat(Stats.ITEM_USED.get(this));
                    syncHand(user, hand);
                    return true;
                }
            }
        }
    }

    private List<ItemStack> getProjectilesFrom(ItemStack weapon, ItemStack projectileSource, Player player, boolean consume) {
        var projectiles = SlingshotEvents.PROJECTILE_PROVIDER.invoker().getProjectilesFrom(weapon, projectileSource, player, consume);
        if (projectiles != null) {
            return projectiles;
        }

        return draw(weapon, consume ? projectileSource : projectileSource.copy(), player);
    }

    private float getSpeed(ItemStack stack, ServerLevel world, LivingEntity user, float progress) {
        var val = new MutableFloat(progress * (1.4f + (stack.is(SlingshotItemTags.EXTRA_PROJECTILE_SPEED) ? 0.5f : 0)));
        for (var ench : EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
            ench.getKey().value().modifyEntityFilteredValue(SlingshotEnchantmentComponents.SLINGSHOT_STRENGTH, world, ench.getIntValue(), stack, user, val);
        }
        return val.floatValue();
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (slot == null || slot.getType() != EquipmentSlot.Type.HAND || !(entity instanceof ServerPlayer player) || player.isSpectator()) {
            return;
        }
        var projectileStack = getProjectileTypeSource(player, stack, slot == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (projectileStack.isEmpty() || projectileStack.slot == -1) {
            return;
        }

        if (projectileStack.slot == -2) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                player.displayClientMessage(Component.literal("-cc" + "a".repeat(11)).setStyle(HOTBAR_OVERLAY_STYLE), true);
            } else {
                player.displayClientMessage(Component.literal("a".repeat(11) + "cc-").setStyle(HOTBAR_OVERLAY_STYLE), true);
            }
        } else {
            player.displayClientMessage(Component.literal("a".repeat(projectileStack.slot) + "-" + "a".repeat(9 - projectileStack.slot - 1)).setStyle(HOTBAR_OVERLAY_STYLE), true);
        }
        ((NetHandlerExt) player.connection).slingshot$setSelectionTick(player.tickCount);
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.onUseTick(world, user, stack, remainingUseTicks);

        if (!(user instanceof ServerPlayer player) || !(world instanceof ServerLevel serverWorld) || player.isSpectator()) {
            return;
        }
        var projectileSource = getProjectileTypeSource(player, stack, user.getUsedItemHand());
        if (projectileSource.isEmpty()) {
            return;
        }

        int useTime = this.getUseDuration(stack, user) - remainingUseTicks;

        if (useTime == 8) {
            world.playSound(null, player.getX(), player.getEyeY(), player.getZ(), SlingshotSoundEvents.ENTITY_SLINGSHOT_LOAD, SoundSource.PLAYERS, 1.0f, 1 + world.getRandom().nextFloat() * 0.2f);
        }

        if (EnchantmentHelper.has(stack, SlingshotEnchantmentComponents.PROJECTILE_PREDICTION)) {
            var mirror = MirrorLevel.get(world);

            float progress = getPullProgress(useTime, stack, user);

            if (progress < 0.3) {
                return;
            }

            try {
                var projectileStacks = this.getProjectilesFrom(stack, projectileSource.stack(), player, false);
                var spread = EnchantmentHelper.processProjectileSpread(serverWorld, stack, player, 0.0F);
                var spreadMult = projectileStacks.size() == 1 ? 0.0F : 2.0F * spread / (float) (projectileStacks.size() - 1);
                var baseSpread = (float) ((projectileStacks.size() - 1) % 2) * spreadMult / 2.0F;
                var side = 1.0f;

                for (var index = 0; index < projectileStacks.size(); index++) {
                    var projectileStack = projectileStacks.get(index);
                    if (projectileStack.isEmpty()) {
                        continue;
                    }
                    float yaw = baseSpread + side * (float) ((index + 1) / 2) * spreadMult;
                    side = -side;

                    var projectile = this.createProjectile(mirror, player, stack, projectileStack, false);
                    SlingshotEnchantments.setBounces(stack, projectile);
                    this.shootProjectile(player, projectile, 0, getSpeed(stack, serverWorld, user, progress), 0, yaw, null);

                    var entity = projectile instanceof FakeProjectileEntity fakeProjectileEntity ? fakeProjectileEntity.entity : projectile;

                    for (int i = 0; i < 100; i++) {
                        entity.tick();
                        var pos = entity.getBoundingBox().getCenter();
                        if (i % 2 == 1 || entity.isRemoved()) {
                            player.connection.send(new ClientboundLevelParticlesPacket(new TrailParticleOption(pos, projectile.isRemoved() ? 0xee0000 : 0xeeeeee, 1), true, true,
                                    pos.x, pos.y, pos.z, 0, 0, 0, 0, 0));
                        }
                        if (entity.isRemoved()) break;
                    }
                }
            } catch (Throwable e) {
                if (ModInit.DEV_MODE || useTime == 20) {
                    ModInit.LOGGER.error("Failed to emulate projectile path!", e);
                }
            }
        }
    }

    public ProjectileStack getProjectileTypeSource(Player player, ItemStack weapon, InteractionHand hand) {
        var predicate = weapon.getOrDefault(SlingshotDataComponents.SLINGSHOT_PROJECTILE_CHECK, Predicates.<ItemStack>alwaysTrue());
        if (hand == InteractionHand.OFF_HAND) {
            var itemStack = player.getMainHandItem();
            if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                return new ProjectileStack(itemStack, player.getInventory().getSelectedSlot());
            }

            /*for (int i = 0; i < 9; i++) {
                itemStack = player.getInventory().getStack(i);
                if (!itemStack.isEmpty() && itemStack != stack) {
                    return new ProjectileStack(itemStack, i);
                }
            }*/

        } else {
            var itemStack = player.getOffhandItem();
            if (!itemStack.isEmpty() && itemStack != weapon && predicate.test(itemStack)) {
                return new ProjectileStack(itemStack, -2);
            }

            /*var il = player.getInventory().getSelectedSlot() - 1;
            var ir = player.getInventory().getSelectedSlot() + 1;

            while (il >= 0 || ir < 9) {
                if (ir < 9) {
                    itemStack = player.getInventory().getStack(ir);
                    if (!itemStack.isEmpty() && itemStack != stack) {
                        return new ProjectileStack(itemStack, ir);
                    }
                    ir++;
                }
                if (il >= 0) {
                    itemStack = player.getInventory().getStack(il);
                    if (!itemStack.isEmpty() && itemStack != stack) {
                        return new ProjectileStack(itemStack, il);
                    }
                    il--;
                }
            }*/
        }

        for (int i = 0; i < 9; i++) {
            var itemStack = player.getInventory().getItem(i);
            if (!itemStack.isEmpty() && itemStack != weapon && predicate.test(itemStack)) {
                return new ProjectileStack(itemStack, i);
            }
        }

        if (player.isCreative()) {
            var stack = SlingshotItems.PEBBLE.getDefaultInstance();
            if (predicate instanceof ItemPredicate itemPredicate) {
                if (itemPredicate.items().isPresent()) {
                    var random = itemPredicate.items().get().getRandomElement(player.getRandom());
                    if (random.isPresent()) {
                        stack = random.get().value().getDefaultInstance();
                    }
                }
                stack.applyComponentsAndValidate(itemPredicate.components().exact().asPatch());
            }

            return new ProjectileStack(stack, -1);
        }

        return ProjectileStack.EMPTY;
    }

    @Override
    protected Projectile createProjectile(Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        if (projectileStack.isEmpty()) {
            projectileStack = SlingshotItems.PEBBLE.getDefaultInstance();
        }
        var pos = shooter.getEyePosition().subtract(0, 0.1, 0);

        if (!EnchantmentHelper.has(weaponStack, SlingshotEnchantmentComponents.PROJECTILE_FORCE_ITEM)) {
            var entity = SlingshotEvents.CREATE_PROJECTILE.invoker().createProjectileEntity(world, pos, shooter, weaponStack, projectileStack);
            if (entity != null) {
                return entity;
            }
            if (projectileStack.getItem() instanceof ArrowItem arrowItem) {
                var projectile = arrowItem.createArrow(world, projectileStack, shooter, weaponStack);
                projectile.setCritArrow(critical);
                return projectile;
            } else if (projectileStack.getItem() instanceof ProjectileItem projectileItem) {
                var projectile = projectileItem.asProjectile(world, pos, projectileStack, shooter.getNearestViewDirection());
                projectile.setOwner(shooter);
                return projectile;
            } else if (projectileStack.is(Items.ENDER_PEARL)) {
                if (!(world instanceof MirrorLevel) && shooter instanceof Player player && projectileStack.has(DataComponents.USE_COOLDOWN)) {
                    player.getCooldowns().addCooldown(weaponStack, Objects.requireNonNull(projectileStack.get(DataComponents.USE_COOLDOWN)).ticks() / 2);
                    player.getCooldowns().addCooldown(projectileStack, Objects.requireNonNull(projectileStack.get(DataComponents.USE_COOLDOWN)).ticks() / 2);
                }

                return new ThrownEnderpearl(world, shooter, projectileStack);
            }
        }

        return ItemProjectileEntity.create(world, pos, projectileStack, weaponStack, shooter);
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot() - (projectile instanceof Fireball ? 0 : 5), shooter.getYRot() + yaw, 0.0F, speed, divergence);
        if (projectile instanceof ItemProjectileEntity entity && index != 0) {
            entity.setReal(false);
        }
        SlingshotEvents.ON_SHOOT.invoker().onShoot(shooter, projectile, index, speed, divergence, yaw, target);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override

    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        /*var l = 1;
        var x = RotationAxis.POSITIVE_X.rotationDegrees(-13.935F);
        var y = RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3F);
        var z = RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785F);

        var out = new Quaternionf()
                .mul(x)
                .mul(y)
                .mul(z)
                .mul(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0F))
                .rotateZ(MathHelper.HALF_PI / 2)
                .invert().getEulerAnglesXYZ(new Vector3f());
        System.out.println(new Vec3d(out.mul(MathHelper.DEGREES_PER_RADIAN)));*/

        var itemStack = user.getItemInHand(hand);
        if (getProjectileTypeSource(user, itemStack, hand).isEmpty()) {
            return InteractionResult.FAIL;
        } else {
            user.startUsingItem(hand);
            syncHand(user, hand);
            return InteractionResult.CONSUME;
        }
    }

    private void syncHand(Entity user, InteractionHand hand) {
        /*if (user instanceof ServerPlayerEntity player && hand != null) {
            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(0, 0,
                    hand == Hand.MAIN_HAND ? PlayerScreenHandler.HOTBAR_START + player.getInventory().getSelectedSlot() : PlayerScreenHandler.OFFHAND_ID, player.getStackInHand(hand)));
        }*/
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return x -> !x.isEmpty();
    }

    @Override
    public int getDefaultProjectileRange() {
        return 13;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        out.set(DataComponents.CONSUMABLE, new Consumable(getUseDuration(stack, null), ItemUseAnimation.BOW, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), false, List.of()));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.TRIAL_KEY;
    }

    public record ProjectileStack(ItemStack stack, int slot) {
        public static ProjectileStack EMPTY = new ProjectileStack(ItemStack.EMPTY, -1);

        public boolean isEmpty() {
            return this.stack.isEmpty();
        }
    }
}
