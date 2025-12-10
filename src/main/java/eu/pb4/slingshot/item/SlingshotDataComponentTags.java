package eu.pb4.slingshot.item;

import eu.pb4.slingshot.ModInit;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;

public class SlingshotDataComponentTags {
    public static final TagKey<DataComponentType<?>> ALWAYS_BLOCK_USABLE_ITEMS = of("always_block_usable_items");
    public static final TagKey<DataComponentType<?>> ALWAYS_ENTITY_USABLE_ITEMS = of("always_entity_usable_items");
    public static final TagKey<DataComponentType<?>> ALWAYS_USABLE_ITEMS = of("always_usable_items");


    public static boolean contains(DataComponentHolder holder, TagKey<DataComponentType<?>> tag) {
        for (var entry : BuiltInRegistries.DATA_COMPONENT_TYPE.getTagOrEmpty(tag)) {
            if (holder.has(entry.value())) {
                return true;
            }
        }
        return false;
    }

    private static TagKey<DataComponentType<?>> of(String path) {
        return TagKey.create(Registries.DATA_COMPONENT_TYPE, ModInit.id(path));
    }
}
