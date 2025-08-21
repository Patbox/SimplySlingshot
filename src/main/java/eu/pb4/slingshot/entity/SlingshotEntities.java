package eu.pb4.slingshot.entity;

import eu.pb4.slingshot.ModInit;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class SlingshotEntities {
    public static final EntityType<ItemProjectileEntity> ITEM_PROJECTILE
            = register("item_projectile", EntityType.Builder.create(ItemProjectileEntity::new, SpawnGroup.MISC)
            .maxTrackingRange(5).trackingTickInterval(1).dimensions(0.5f, 0.5f));

    public static final EntityType<FakeProjectileEntity> FAKE_PROJECTILE
            = register("fake_projectile", EntityType.Builder.create(FakeProjectileEntity::new, SpawnGroup.MISC)
            .maxTrackingRange(0).trackingTickInterval(1).dimensions(0.1f, 0.1f));

    public static void register() {
    }

    public static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> item) {
        var id = Identifier.of(ModInit.ID, path);
        var x = Registry.register(Registries.ENTITY_TYPE, id, item.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)));
        PolymerEntityUtils.registerType(x);
        return x;
    }
}
