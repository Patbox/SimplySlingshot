package eu.pb4.slingshot.item;

import com.mojang.serialization.Codec;
import eu.pb4.slingshot.ModInit;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import eu.pb4.polymer.core.api.other.PolymerComponent;

public class SlingshotDataComponents {
    public static final DataComponentType<Float> SLINGSHOT_WEAPON_DAMAGE = register("slingshot_weapon_damage", DataComponentType.<Float>builder().persistent(Codec.FLOAT));
    public static final DataComponentType<Float> SLINGSHOT_WEAPON_KNOCKBACK_BONUS = register("slingshot_weapon_knockback_bonus", DataComponentType.<Float>builder().persistent(Codec.FLOAT));
    public static final DataComponentType<Float> SLINGSHOT_PROJECTILE_DAMAGE_BONUS = register("slingshot_projectile_damage_bonus", DataComponentType.<Float>builder().persistent(Codec.FLOAT));
    public static final DataComponentType<Float> SLINGSHOT_PROJECTILE_KNOCKBACK_BONUS = register("slingshot_projectile_knockback_bonus", DataComponentType.<Float>builder().persistent(Codec.FLOAT));
    public static final DataComponentType<ItemPredicate> SLINGSHOT_PROJECTILE_CHECK = register("slingshot_projectile_check", DataComponentType.<ItemPredicate>builder().persistent(ItemPredicate.CODEC));
    public static <T> DataComponentType<T> register(String path, DataComponentType.Builder<T> builder) {
        var x =  Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(ModInit.ID, path), builder.build());
        PolymerComponent.registerDataComponent(x);
        return x;
    }
}
