package eu.pb4.slingshot.item;

import eu.pb4.slingshot.ModInit;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SlingshotItemTags {
    public static final TagKey<Item> ENCHANTMENT_USABLE_ITEMS = of("enchantment_usable_tools");
    public static final TagKey<Item> ALWAYS_BLOCK_USABLE_ITEMS = of("always_block_usable_items");
    public static final TagKey<Item> ALWAYS_ENTITY_USABLE_ITEMS = of("always_entity_usable_items");
    public static final TagKey<Item> ALWAYS_USABLE_ITEMS = of("always_usable_items");
    public static final TagKey<Item> BRICK_LIKE = of("brick_like");
    public static final TagKey<Item> SLIME_LIKE = of("slime_like");
    public static final TagKey<Item> LOW_PROJECTILE_DAMAGE = of("low_projectile_damage");
    public static final TagKey<Item> MEDIUM_PROJECTILE_DAMAGE = of("medium_projectile_damage");
    public static final TagKey<Item> HIGH_PROJECTILE_DAMAGE = of("high_projectile_damage");
    public static final TagKey<Item> ROTATE_LIKE_ITEM_ROD = of("rotate_like_item_rod");
    public static final TagKey<Item> ROTATE_LIKE_BLOCK_ROD = of("rotate_like_block_rod");
    public static final TagKey<Item> EXTRA_PROJECTILE_SPEED = of("extra_projectile_speed");

    private static TagKey<Item> of(String path) {
        return TagKey.of(RegistryKeys.ITEM, ModInit.id(path));
    }
}
