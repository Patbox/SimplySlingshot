package eu.pb4.slingshot.entity;

import eu.pb4.slingshot.ModInit;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;

public class SlingshotEntities {
    public static final EntityType<ItemProjectileEntity> ITEM_PROJECTILE
            = register("item_projectile", EntityType.Builder.of(ItemProjectileEntity::new, MobCategory.MISC)
            .clientTrackingRange(5).updateInterval(1).sized(0.5f, 0.5f));

    public static final EntityType<FakeProjectileEntity> FAKE_PROJECTILE
            = register("fake_projectile", EntityType.Builder.of(FakeProjectileEntity::new, MobCategory.MISC)
            .clientTrackingRange(0).updateInterval(1).sized(0.1f, 0.1f));

    public static void register() {
    }

    public static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> item) {
        var id = Identifier.fromNamespaceAndPath(ModInit.ID, path);
        var x = Registry.register(BuiltInRegistries.ENTITY_TYPE, id, item.build(ResourceKey.create(Registries.ENTITY_TYPE, id)));
        PolymerEntityUtils.registerType(x);
        return x;
    }
}
