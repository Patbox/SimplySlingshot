package eu.pb4.slingshot.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.slingshot.ModInit;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class SlingshotEntityIds {
    public static final ResourceKey<EntityType<?>> ITEM_PROJECTILE = register("item_projectile");

    public static final ResourceKey<EntityType<?>> FAKE_PROJECTILE = register("fake_projectile");


    public static ResourceKey<EntityType<?>> register(String path) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ModInit.ID, path));
    }
}
