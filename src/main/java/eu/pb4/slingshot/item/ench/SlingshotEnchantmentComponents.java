package eu.pb4.slingshot.item.ench;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.slingshot.ModInit;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import java.util.List;

public class SlingshotEnchantmentComponents {
    public static DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> SLINGSHOT_STRENGTH = register("slingshot_strength",
            DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder().persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()));
    public static DataComponentType<EnchantmentValueEffect> PROJECTILE_BOUNCE = register("projectile_bounce", DataComponentType.<EnchantmentValueEffect>builder().persistent(EnchantmentValueEffect.CODEC));
    public static DataComponentType<Unit> PROJECTILE_PREDICTION = register("projectile_prediction", DataComponentType.<Unit>builder().persistent(Unit.CODEC));
    public static DataComponentType<Unit> PROJECTILE_BLOCK_PLACER = register("projectile_block_placer", DataComponentType.<Unit>builder().persistent(Unit.CODEC));
    public static DataComponentType<Unit> PROJECTILE_TOOL_USER = register("projectile_tool_user", DataComponentType.<Unit>builder().persistent(Unit.CODEC));
    public static DataComponentType<Unit> PROJECTILE_BOOMERANG_ENTITY = register("projectile_boomerang_entity", DataComponentType.<Unit>builder().persistent(Unit.CODEC));
    public static DataComponentType<Unit> PROJECTILE_BOOMERANG_BLOCK = register("projectile_boomerang_block", DataComponentType.<Unit>builder().persistent(Unit.CODEC));
    public static DataComponentType<Unit> PROJECTILE_FORCE_ITEM = register("projectile_force_item", DataComponentType.<Unit>builder().persistent(Unit.CODEC));
    public static DataComponentType<Unit> PROJECTILE_ITEM_NO_SIDE_EFFECTS = register("projectile_item_no_side_effects", DataComponentType.<Unit>builder().persistent(Unit.CODEC));
    public static <T> DataComponentType<T> register(String path, DataComponentType.Builder<T> builder) {
        var x =  Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(ModInit.ID, path), builder.build());
        PolymerComponent.registerDataComponent(x);
        return x;
    }

    public static void register() {
    }
}
