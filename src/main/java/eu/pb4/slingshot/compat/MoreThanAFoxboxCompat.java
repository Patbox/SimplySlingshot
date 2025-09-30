package eu.pb4.slingshot.compat;

import dev.sweetberry.more_than_a_foxbox.item.PlushieItem;
import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.entity.ItemProjectileEntity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public record MoreThanAFoxboxCompat() implements SlingshotEvents.OnBounce, SlingshotEvents.ItemProjectileGenericHit {
    public MoreThanAFoxboxCompat {
        SlingshotEvents.ITEM_PROJECTILE_GENERIC_HIT.register(this);
        SlingshotEvents.ON_BOUNCE.register(this);
    }

    @Override
    public boolean onGenericHit(ItemProjectileEntity projectile, HitResult result) {
        if (projectile.getStack().getItem() instanceof PlushieItem plushieItem) {
            var sound = plushieItem.getInteractionSound(projectile.getEntityWorld().getRegistryManager(), projectile.getStack());
            sound.ifPresent(soundEvent -> projectile.getEntityWorld().playSoundFromEntity(null, projectile, soundEvent, SoundCategory.NEUTRAL, 1.0F, 1.0F));
        }
        return false;
    }

    @Override
    public void onBounce(ProjectileEntity projectile, Vec3d originalVelocity, HitResult result) {
        if (projectile instanceof FlyingItemEntity entity && entity.getStack().getItem() instanceof PlushieItem plushieItem) {
            var sound = plushieItem.getInteractionSound(projectile.getEntityWorld().getRegistryManager(), entity.getStack());
            sound.ifPresent(soundEvent -> projectile.getEntityWorld().playSoundFromEntity(null, projectile, soundEvent, SoundCategory.NEUTRAL, 1.0F, 1.0F));
        }
    }
}
