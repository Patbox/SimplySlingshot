package eu.pb4.slingshot.item.ench;

import eu.pb4.slingshot.ModInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class SlingshotEnchantmentTags {

    public static final TagKey<Enchantment> ITEM_SENDER_INCOMPATIBLE = of("item_sender_incompatible");
    private static TagKey<Enchantment> of(String path) {
        return TagKey.create(Registries.ENCHANTMENT, ModInit.id(path));
    }
}
