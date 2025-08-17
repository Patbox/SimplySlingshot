package eu.pb4.slingshot.item.ench;

import eu.pb4.slingshot.ModInit;
import eu.pb4.slingshot.item.SlingshotItems;
import eu.pb4.slingshot.util.BounceableExt;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableFloat;

public class SlingshotEnchantments {
    public static final RegistryKey<Enchantment> BOUNCE = of("bounce");
    public static final RegistryKey<Enchantment> BLOCK_PLACER = of("block_placer");
    public static final RegistryKey<Enchantment> TOOL_USER = of("tool_user");
    public static final RegistryKey<Enchantment> TRAJECTORY_PREDICTION = of("trajectory_prediction");
    public static final RegistryKey<Enchantment> BOOMERANG = of("boomerang");
    public static final RegistryKey<Enchantment> ITEM_SENDER = of("item_sender");

    private static RegistryKey<Enchantment> of(String path) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, ModInit.id(path));
    }

    public static void register() {
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, enchantingContext) -> {
            if (!enchantment.getKey().orElseThrow().equals(Enchantments.QUICK_CHARGE)) return TriState.DEFAULT;
            return target.isOf(SlingshotItems.SLINGSHOT) ? TriState.TRUE : TriState.DEFAULT;
        });
    }

    public static void setBounces(ItemStack stack, ProjectileEntity projectile) {
        ((BounceableExt) projectile).slingshot$setBounces(getBounces(stack, ((BounceableExt) projectile).slingshot$getBounces(), projectile.getRandom()));
    }

    public static int getBounces(ItemStack stack, int initial, Random random) {
        var val = new MutableFloat(initial);
        for (var ench : EnchantmentHelper.getEnchantments(stack).getEnchantmentEntries()) {
            ench.getKey().value().modifyValue(SlingshotEnchantmentComponents.PROJECTILE_BOUNCE, random, ench.getIntValue(), val);
        }
        return val.intValue();
    }
}
