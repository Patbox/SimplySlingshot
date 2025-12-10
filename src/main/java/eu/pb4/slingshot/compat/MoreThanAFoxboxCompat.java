package eu.pb4.slingshot.compat;

import dev.sweetberry.more_than_a_foxbox.item.PlushieItem;
import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.entity.ItemProjectileEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record MoreThanAFoxboxCompat() implements SlingshotEvents.OnBounce, SlingshotEvents.ItemProjectileGenericHit {
    public MoreThanAFoxboxCompat {
        SlingshotEvents.ITEM_PROJECTILE_GENERIC_HIT.register(this);
        SlingshotEvents.ON_BOUNCE.register(this);
    }

    @Override
    public boolean onGenericHit(ItemProjectileEntity projectile, HitResult result) {
        if (projectile.getItem().getItem() instanceof PlushieItem plushieItem) {
            var sound = plushieItem.getInteractionSound(projectile.level().registryAccess(), projectile.getItem());
            sound.ifPresent(soundEvent -> projectile.level().playSound(null, projectile, soundEvent, SoundSource.NEUTRAL, 1.0F, 1.0F));
        }
        return false;
    }

    @Override
    public void onBounce(Projectile projectile, Vec3 originalVelocity, HitResult result) {
        if (projectile instanceof ItemSupplier entity && entity.getItem().getItem() instanceof PlushieItem plushieItem) {
            var sound = plushieItem.getInteractionSound(projectile.level().registryAccess(), entity.getItem());
            sound.ifPresent(soundEvent -> projectile.level().playSound(null, projectile, soundEvent, SoundSource.NEUTRAL, 1.0F, 1.0F));
        }
    }
}
