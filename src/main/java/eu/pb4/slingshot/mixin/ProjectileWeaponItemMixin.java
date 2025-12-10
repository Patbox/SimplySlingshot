package eu.pb4.slingshot.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.slingshot.item.ench.SlingshotEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponItemMixin {
    @WrapOperation(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;createProjectile(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile updateBounces(ProjectileWeaponItem instance, Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical, Operation<Projectile> original) {
        var projectile = original.call(instance, world, shooter, weaponStack, projectileStack, critical);
        SlingshotEnchantments.setBounces(weaponStack, projectile);
        return projectile;
    }
}
