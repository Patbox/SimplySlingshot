package eu.pb4.slingshot.item.ench;

import eu.pb4.slingshot.ModInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SlingshotEnchantmentTags {

    public static final TagKey<Enchantment> ITEM_SENDER_INCOMPATIBLE = of("item_sender_incompatible");
    private static TagKey<Enchantment> of(String path) {
        return TagKey.of(RegistryKeys.ENCHANTMENT, ModInit.id(path));
    }
}
