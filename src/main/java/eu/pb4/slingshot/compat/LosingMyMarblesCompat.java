package eu.pb4.slingshot.compat;

import eu.pb4.slingshot.ModInit;
import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.entity.FakeProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import one.devos.nautical.losing_my_marbles.content.LosingMyMarblesDataComponents;
import one.devos.nautical.losing_my_marbles.content.LosingMyMarblesEntities;
import one.devos.nautical.losing_my_marbles.content.LosingMyMarblesItems;
import one.devos.nautical.losing_my_marbles.content.marble.MarbleEntity;
import one.devos.nautical.losing_my_marbles.content.marble.StoredMarble;
import org.jetbrains.annotations.Nullable;

public record LosingMyMarblesCompat() implements SlingshotEvents.CreateProjectile {
    public LosingMyMarblesCompat {
        SlingshotEvents.CREATE_PROJECTILE.register(this);
    }

    @Override
    public @Nullable ProjectileEntity createProjectileEntity(World world, Vec3d pos, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack) {
        try {
            if (projectileStack.isOf(LosingMyMarblesItems.MARBLE)) {
                StoredMarble marble = projectileStack.get(LosingMyMarblesDataComponents.MARBLE);
                if (marble == null) {
                    return null;
                } else {
                    var instance = marble.get(world.getRegistryManager());
                    if (instance.isEmpty()) {
                        return null;
                    }
                    var marbleEntity = new MarbleEntity(LosingMyMarblesEntities.MARBLE, world, instance.orElseThrow());
                    marbleEntity.setOwner(shooter);
                    return instance.map(marbleInstance -> FakeProjectileEntity.wrap(world, pos, marbleEntity)).orElse(null);
                }
            }
        } catch (Throwable e) {
            ModInit.LOGGER.error("Failed to shoot out a marble!", e);
        }
        return null;
    }
}
