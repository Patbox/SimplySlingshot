package eu.pb4.slingshot.item;

import com.mojang.serialization.Codec;
import eu.pb4.slingshot.ModInit;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SlingshotDataComponents {
    public static final ComponentType<Float> SLINGSHOT_WEAPON_DAMAGE = register("slingshot_weapon_damage", ComponentType.<Float>builder().codec(Codec.FLOAT));
    public static final ComponentType<Float> SLINGSHOT_WEAPON_KNOCKBACK_BONUS = register("slingshot_weapon_knockback_bonus", ComponentType.<Float>builder().codec(Codec.FLOAT));
    public static final ComponentType<Float> SLINGSHOT_PROJECTILE_DAMAGE_BONUS = register("slingshot_projectile_damage_bonus", ComponentType.<Float>builder().codec(Codec.FLOAT));
    public static final ComponentType<Float> SLINGSHOT_PROJECTILE_KNOCKBACK_BONUS = register("slingshot_projectile_knockback_bonus", ComponentType.<Float>builder().codec(Codec.FLOAT));
    public static final ComponentType<ItemPredicate> SLINGSHOT_PROJECTILE_CHECK = register("slingshot_projectile_check", ComponentType.<ItemPredicate>builder().codec(ItemPredicate.CODEC));
    public static <T> ComponentType<T> register(String path, ComponentType.Builder<T> builder) {
        var x =  Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(ModInit.ID, path), builder.build());
        PolymerComponent.registerDataComponent(x);
        return x;
    }
}
