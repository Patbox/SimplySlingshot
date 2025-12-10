package eu.pb4.slingshot.item.ench;

import eu.pb4.slingshot.ModInit;
import eu.pb4.slingshot.item.SlingshotItems;
import eu.pb4.slingshot.util.BounceableExt;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.apache.commons.lang3.mutable.MutableFloat;

public class SlingshotEnchantments {
    public static final ResourceKey<Enchantment> BOUNCE = of("bounce");
    public static final ResourceKey<Enchantment> BLOCK_PLACER = of("block_placer");
    public static final ResourceKey<Enchantment> TOOL_USER = of("tool_user");
    public static final ResourceKey<Enchantment> TRAJECTORY_PREDICTION = of("trajectory_prediction");
    public static final ResourceKey<Enchantment> BOOMERANG = of("boomerang");
    public static final ResourceKey<Enchantment> ITEM_SENDER = of("item_sender");

    private static ResourceKey<Enchantment> of(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, ModInit.id(path));
    }

    public static void register() {
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, enchantingContext) -> {
            if (!enchantment.unwrapKey().orElseThrow().equals(Enchantments.QUICK_CHARGE)) return TriState.DEFAULT;
            return target.is(SlingshotItems.SLINGSHOT) ? TriState.TRUE : TriState.DEFAULT;
        });
    }

    public static void setBounces(ItemStack stack, Projectile projectile) {
        ((BounceableExt) projectile).slingshot$setBounces(getBounces(stack, ((BounceableExt) projectile).slingshot$getBounces(), projectile.getRandom()));
    }

    public static int getBounces(ItemStack stack, int initial, RandomSource random) {
        var val = new MutableFloat(initial);
        for (var ench : EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
            ench.getKey().value().modifyUnfilteredValue(SlingshotEnchantmentComponents.PROJECTILE_BOUNCE, random, ench.getIntValue(), val);
        }
        return val.intValue();
    }
}
