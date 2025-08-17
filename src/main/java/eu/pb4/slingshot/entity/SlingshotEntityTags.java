package eu.pb4.slingshot.entity;

import eu.pb4.slingshot.ModInit;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SlingshotEntityTags {
    private static TagKey<EntityType<?>> of(String path) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, ModInit.id(path));
    }
}
