package eu.pb4.slingshot.entity;

import eu.pb4.slingshot.ModInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class SlingshotEntityTags {
    private static TagKey<EntityType<?>> of(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ModInit.id(path));
    }
}
