package eu.pb4.slingshot.compat;

import eu.pb4.slingshot.ModInit;
import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.entity.FakeProjectileEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    public @Nullable Projectile createProjectileEntity(Level world, Vec3 pos, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack) {
        try {
            if (projectileStack.is(LosingMyMarblesItems.MARBLE)) {
                StoredMarble marble = projectileStack.get(LosingMyMarblesDataComponents.MARBLE);
                if (marble == null) {
                    return null;
                } else {
                    var instance = marble.get(world.registryAccess());
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
