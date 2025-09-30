package eu.pb4.slingshot.item;

import com.google.common.base.Predicates;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.slingshot.ModInit;
import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.entity.FakeProjectileEntity;
import eu.pb4.slingshot.entity.ItemProjectileEntity;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentComponents;
import eu.pb4.slingshot.item.ench.SlingshotEnchantments;
import eu.pb4.slingshot.mixin.EnchantmentAccessor;
import eu.pb4.slingshot.util.MirrorWorld;
import eu.pb4.slingshot.util.NetHandlerExt;
import eu.pb4.slingshot.util.SlingshotSoundEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static eu.pb4.slingshot.ModInit.id;

public class SlingshotItem extends RangedWeaponItem implements PolymerItem {
    private static final Style HOTBAR_OVERLAY_STYLE = Style.EMPTY.withFont(new StyleSpriteSource.Font(id("hotbar_overlay"))).withShadowColor(0);

    public SlingshotItem(Settings settings) {
        super(settings);
    }

    public static float getPullProgress(int useTicks, ItemStack stack, LivingEntity user) {
        var f = useTicks / 20.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        f /= EnchantmentHelper.getCrossbowChargeTime(stack, user, 1);

        if (f > 1.0f) {
            f = 1.0f;
        }

        return f;
    }

    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        var hand = user.getStackInHand(Hand.MAIN_HAND) == stack ? Hand.MAIN_HAND : user.getStackInHand(Hand.OFF_HAND) == stack ? Hand.OFF_HAND : null;
        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        } else {
            var itemStack = getProjectileTypeSource(playerEntity, stack, user.getActiveHand());
            if (itemStack.isEmpty()) {
                syncHand(user, hand);
                return false;
            } else {
                var useTime = this.getMaxUseTime(stack, user) - remainingUseTicks;
                float progress = getPullProgress(useTime, stack, user);
                if (progress < 0.3) {
                    syncHand(user, hand);
                    return false;
                } else {
                    var list = this.getProjectilesFrom(stack, itemStack.stack(), playerEntity, true);
                    if (world instanceof ServerWorld serverWorld) {
                        if (!list.isEmpty()) {
                            this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, getSpeed(stack, serverWorld, user, progress), 1.0F, false, null);
                        }
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SlingshotSoundEvents.ENTITY_SLINGSHOT_SHOOT, SoundCategory.PLAYERS, 1.0F, 1 / (world.getRandom().nextFloat() * 0.4F + 1.2F) + progress * 0.5F);
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    syncHand(user, hand);
                    return true;
                }
            }
        }
    }

    private List<ItemStack> getProjectilesFrom(ItemStack weapon, ItemStack projectileSource, PlayerEntity player, boolean consume) {
        var projectiles = SlingshotEvents.PROJECTILE_PROVIDER.invoker().getProjectilesFrom(weapon, projectileSource, player, consume);
        if (projectiles != null) {
            return projectiles;
        }

        return load(weapon, consume ? projectileSource : projectileSource.copy(), player);
    }

    private float getSpeed(ItemStack stack, ServerWorld world, LivingEntity user, float progress) {
        var val = new MutableFloat(progress * (1.4f + (stack.isIn(SlingshotItemTags.EXTRA_PROJECTILE_SPEED) ? 0.5f : 0)));
        for (var ench : EnchantmentHelper.getEnchantments(stack).getEnchantmentEntries()) {
            ((EnchantmentAccessor) (Object) ench.getKey().value()).callModifyValue(SlingshotEnchantmentComponents.SLINGSHOT_STRENGTH, world, ench.getIntValue(), stack, user, val);
        }
        return val.floatValue();
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (slot == null || slot.getType() != EquipmentSlot.Type.HAND || !(entity instanceof ServerPlayerEntity player) || player.isSpectator()) {
            return;
        }
        var projectileStack = getProjectileTypeSource(player, stack, slot == EquipmentSlot.MAINHAND ? Hand.MAIN_HAND : Hand.OFF_HAND);
        if (projectileStack.isEmpty() || projectileStack.slot == -1) {
            return;
        }

        if (projectileStack.slot == -2) {
            if (player.getMainArm() == Arm.RIGHT) {
                player.sendMessage(Text.literal("-cc" + "a".repeat(11)).setStyle(HOTBAR_OVERLAY_STYLE), true);
            } else {
                player.sendMessage(Text.literal("a".repeat(11) + "cc-").setStyle(HOTBAR_OVERLAY_STYLE), true);
            }
        } else {
            player.sendMessage(Text.literal("a".repeat(projectileStack.slot) + "-" + "a".repeat(9 - projectileStack.slot - 1)).setStyle(HOTBAR_OVERLAY_STYLE), true);
        }
        ((NetHandlerExt) player.networkHandler).slingshot$setSelectionTick(player.age);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);

        if (!(user instanceof ServerPlayerEntity player) || !(world instanceof ServerWorld serverWorld) || player.isSpectator()) {
            return;
        }
        var projectileSource = getProjectileTypeSource(player, stack, user.getActiveHand());
        if (projectileSource.isEmpty()) {
            return;
        }

        int useTime = this.getMaxUseTime(stack, user) - remainingUseTicks;

        if (useTime == 8) {
            world.playSound(null, player.getX(), player.getEyeY(), player.getZ(), SlingshotSoundEvents.ENTITY_SLINGSHOT_LOAD, SoundCategory.PLAYERS, 1.0f, 1 + world.getRandom().nextFloat() * 0.2f);
        }

        if (EnchantmentHelper.hasAnyEnchantmentsWith(stack, SlingshotEnchantmentComponents.PROJECTILE_PREDICTION)) {
            var mirror = MirrorWorld.get(world);

            float progress = getPullProgress(useTime, stack, user);

            if (progress < 0.3) {
                return;
            }

            try {
                var projectileStacks = this.getProjectilesFrom(stack, projectileSource.stack(), player, false);
                var spread = EnchantmentHelper.getProjectileSpread(serverWorld, stack, player, 0.0F);
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

                    var projectile = this.createArrowEntity(mirror, player, stack, projectileStack, false);
                    SlingshotEnchantments.setBounces(stack, projectile);
                    this.shoot(player, projectile, 0, getSpeed(stack, serverWorld, user, progress), 0, yaw, null);

                    var entity = projectile instanceof FakeProjectileEntity fakeProjectileEntity ? fakeProjectileEntity.entity : projectile;

                    for (int i = 0; i < 100; i++) {
                        entity.tick();
                        var pos = entity.getBoundingBox().getCenter();
                        if (i % 2 == 1 || entity.isRemoved()) {
                            player.networkHandler.sendPacket(new ParticleS2CPacket(new TrailParticleEffect(pos, projectile.isRemoved() ? 0xee0000 : 0xeeeeee, 1), true, true,
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

    public ProjectileStack getProjectileTypeSource(PlayerEntity player, ItemStack weapon, Hand hand) {
        var predicate = weapon.getOrDefault(SlingshotDataComponents.SLINGSHOT_PROJECTILE_CHECK, Predicates.<ItemStack>alwaysTrue());
        if (hand == Hand.OFF_HAND) {
            var itemStack = player.getMainHandStack();
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
            var itemStack = player.getOffHandStack();
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
            var itemStack = player.getInventory().getStack(i);
            if (!itemStack.isEmpty() && itemStack != weapon && predicate.test(itemStack)) {
                return new ProjectileStack(itemStack, i);
            }
        }

        if (player.isCreative()) {
            var stack = SlingshotItems.PEBBLE.getDefaultStack();
            if (predicate instanceof ItemPredicate itemPredicate) {
                if (itemPredicate.items().isPresent()) {
                    var random = itemPredicate.items().get().getRandom(player.getRandom());
                    if (random.isPresent()) {
                        stack = random.get().value().getDefaultStack();
                    }
                }
                stack.applyChanges(itemPredicate.components().exact().toChanges());
            }

            return new ProjectileStack(stack, -1);
        }

        return ProjectileStack.EMPTY;
    }

    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        if (projectileStack.isEmpty()) {
            projectileStack = SlingshotItems.PEBBLE.getDefaultStack();
        }
        var pos = shooter.getEyePos().subtract(0, 0.1, 0);

        if (!EnchantmentHelper.hasAnyEnchantmentsWith(weaponStack, SlingshotEnchantmentComponents.PROJECTILE_FORCE_ITEM)) {
            var entity = SlingshotEvents.CREATE_PROJECTILE.invoker().createProjectileEntity(world, pos, shooter, weaponStack, projectileStack);
            if (entity != null) {
                return entity;
            }
            if (projectileStack.getItem() instanceof ArrowItem arrowItem) {
                var projectile = arrowItem.createArrow(world, projectileStack, shooter, weaponStack);
                projectile.setCritical(critical);
                return projectile;
            } else if (projectileStack.getItem() instanceof ProjectileItem projectileItem) {
                var projectile = projectileItem.createEntity(world, pos, projectileStack, shooter.getFacing());
                projectile.setOwner(shooter);
                return projectile;
            } else if (projectileStack.isOf(Items.ENDER_PEARL)) {
                if (!(world instanceof MirrorWorld) && shooter instanceof PlayerEntity player && projectileStack.contains(DataComponentTypes.USE_COOLDOWN)) {
                    player.getItemCooldownManager().set(weaponStack, Objects.requireNonNull(projectileStack.get(DataComponentTypes.USE_COOLDOWN)).getCooldownTicks() / 2);
                    player.getItemCooldownManager().set(projectileStack, Objects.requireNonNull(projectileStack.get(DataComponentTypes.USE_COOLDOWN)).getCooldownTicks() / 2);
                }

                return new EnderPearlEntity(world, shooter, projectileStack);
            }
        }

        return ItemProjectileEntity.create(world, pos, projectileStack, weaponStack, shooter);
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        projectile.setVelocity(shooter, shooter.getPitch() - (projectile instanceof AbstractFireballEntity ? 0 : 5), shooter.getYaw() + yaw, 0.0F, speed, divergence);
        if (projectile instanceof ItemProjectileEntity entity && index != 0) {
            entity.setReal(false);
        }
        SlingshotEvents.ON_SHOOT.invoker().onShoot(shooter, projectile, index, speed, divergence, yaw, target);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
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

        var itemStack = user.getStackInHand(hand);
        if (getProjectileTypeSource(user, itemStack, hand).isEmpty()) {
            return ActionResult.FAIL;
        } else {
            user.setCurrentHand(hand);
            syncHand(user, hand);
            return ActionResult.CONSUME;
        }
    }

    private void syncHand(Entity user, Hand hand) {
        /*if (user instanceof ServerPlayerEntity player && hand != null) {
            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(0, 0,
                    hand == Hand.MAIN_HAND ? PlayerScreenHandler.HOTBAR_START + player.getInventory().getSelectedSlot() : PlayerScreenHandler.OFFHAND_ID, player.getStackInHand(hand)));
        }*/
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return x -> !x.isEmpty();
    }

    @Override
    public int getRange() {
        return 13;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        out.set(DataComponentTypes.CONSUMABLE, new ConsumableComponent(getMaxUseTime(stack, null), UseAction.BOW, Registries.SOUND_EVENT.getEntry(SoundEvents.INTENTIONALLY_EMPTY), false, List.of()));
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
