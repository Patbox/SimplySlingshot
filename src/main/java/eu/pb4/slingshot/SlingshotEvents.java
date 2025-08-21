package eu.pb4.slingshot;

import eu.pb4.slingshot.entity.ItemProjectileEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SlingshotEvents {
    Event<CreateProjectile> CREATE_PROJECTILE = EventFactory.createArrayBacked(CreateProjectile.class,
            (world, pos, shooter, weapon, projectile) -> null,
            arr -> (world, pos, shooter, weapon, projectile) -> {
                for (var a : arr) {
                    var x = a.createProjectileEntity(world, pos, shooter, weapon, projectile);
                    if (x != null) {
                        return x;
                    }
                }
                return null;
            });

    Event<ProjectileProvider> PROJECTILE_PROVIDER = EventFactory.createArrayBacked(ProjectileProvider.class,
            (weapon, projectile, shooter, consume) -> null,
            arr -> (weapon, projectile, shooter, consume) -> {
                for (var a : arr) {
                    var x = a.getProjectilesFrom(weapon, projectile, shooter, consume);
                    if (x != null) {
                        return x;
                    }
                }
                return null;
            });

    Event<Shoot> ON_SHOOT = EventFactory.createArrayBacked(Shoot.class,
            (shooter, projectile, index, speed, divergence, yaw, target) -> {
            },
            arr -> (shooter, projectile, index, speed, divergence, yaw, target) -> {
                for (var a : arr) {
                    a.onShoot(shooter, projectile, index, speed, divergence, yaw, target);
                }
            });

    Event<ItemProjectileBlockHit> ITEM_PROJECTILE_BLOCK_HIT = EventFactory.createArrayBacked(ItemProjectileBlockHit.class,
            (entity, result) -> false,
            arr -> (entity, result) -> {
                for (var a : arr) {
                    if (a.onBlockHit(entity, result)) {
                        return true;
                    }
                }
                return false;
            });

    Event<ItemProjectileEntityHit> ITEM_PROJECTILE_ENTITY_HIT = EventFactory.createArrayBacked(ItemProjectileEntityHit.class,
            (entity, result) -> false,
            arr -> (entity, result) -> {
                for (var a : arr) {
                    if (a.onEntityHit(entity, result)) {
                        return true;
                    }
                }
                return false;
            });

    Event<ItemProjectileGenericHit> ITEM_PROJECTILE_GENERIC_HIT = EventFactory.createArrayBacked(ItemProjectileGenericHit.class,
            (entity, result) -> false,
            arr -> (entity, result) -> {
                for (var a : arr) {
                    if (a.onGenericHit(entity, result)) {
                        return true;
                    }
                }
                return false;
            });

    Event<ItemProjectileTryDropSelf> ITEM_PROJECTILE_TRY_DROP_SELF = EventFactory.createArrayBacked(ItemProjectileTryDropSelf.class,
            (entity, world, offset, shouldReturn) -> false,
            arr -> (entity, world, offset, shouldReturn) -> {
                for (var a : arr) {
                    if (a.onTryDropSelf(entity, world, offset, shouldReturn)) {
                        return true;
                    }
                }
                return false;
            });

    Event<OnBounce> ON_BOUNCE = EventFactory.createArrayBacked(OnBounce.class,
            (entity, oldVec, result) -> {},
            arr -> (entity, oldVec, result) -> {
                for (var a : arr) {
                    a.onBounce(entity, oldVec, result);
                }
            });


    interface CreateProjectile {
        @Nullable
        ProjectileEntity createProjectileEntity(World world, Vec3d pos, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack);
    }

    interface ProjectileProvider {
        @Nullable
        List<ItemStack> getProjectilesFrom(ItemStack weapon, ItemStack projectileSource, PlayerEntity player, boolean consume);
    }

    interface Shoot {
        void onShoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target);
    }

    interface ItemProjectileBlockHit {
        boolean onBlockHit(ItemProjectileEntity projectile, BlockHitResult result);
    }

    interface ItemProjectileEntityHit {
        boolean onEntityHit(ItemProjectileEntity projectile, EntityHitResult result);
    }

    interface ItemProjectileGenericHit {
        boolean onGenericHit(ItemProjectileEntity projectile, HitResult result);
    }

    interface ItemProjectileTryDropSelf {
        boolean onTryDropSelf(ItemProjectileEntity projectile, ServerWorld world, Vec3d offset, boolean shouldReturn);
    }

    interface OnBounce {
        void onBounce(ProjectileEntity projectile, Vec3d originalVelocity, HitResult result);
    }
}
