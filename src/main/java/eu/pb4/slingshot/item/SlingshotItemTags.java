package eu.pb4.slingshot.item;

import eu.pb4.slingshot.ModInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

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
    public static final TagKey<Item> ROTATE_ON_Y_AXIS_45_DEG = of("rotate_on_y_axis_45_deg");
    public static final TagKey<Item> ROTATE_ON_Y_AXIS_N45_DEG = of("rotate_on_y_axis_n45_deg");
    public static final TagKey<Item> ROTATE_ON_Y_AXIS_180_DEG = of("rotate_on_y_axis_180_deg");
    public static final TagKey<Item> ROTATE_ON_Y_AXIS = of("rotate_on_y_axis");
    public static final TagKey<Item> EXTRA_PROJECTILE_SPEED = of("extra_projectile_speed");

    private static TagKey<Item> of(String path) {
        return TagKey.create(Registries.ITEM, ModInit.id(path));
    }
}
