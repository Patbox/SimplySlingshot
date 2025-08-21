package eu.pb4.slingshot.item;

import eu.pb4.slingshot.ModInit;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SlingshotDataComponentTags {
    public static final TagKey<ComponentType<?>> ALWAYS_BLOCK_USABLE_ITEMS = of("always_block_usable_items");
    public static final TagKey<ComponentType<?>> ALWAYS_ENTITY_USABLE_ITEMS = of("always_entity_usable_items");
    public static final TagKey<ComponentType<?>> ALWAYS_USABLE_ITEMS = of("always_usable_items");


    public static boolean contains(ComponentHolder holder, TagKey<ComponentType<?>> tag) {
        for (var entry : Registries.DATA_COMPONENT_TYPE.iterateEntries(tag)) {
            if (holder.contains(entry.value())) {
                return true;
            }
        }
        return false;
    }

    private static TagKey<ComponentType<?>> of(String path) {
        return TagKey.of(RegistryKeys.DATA_COMPONENT_TYPE, ModInit.id(path));
    }
}
