package eu.pb4.slingshot.item.ench;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.slingshot.ModInit;
import net.minecraft.component.ComponentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.context.ContextType;

import java.util.List;

public class SlingshotEnchantmentComponents {
    public static ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> SLINGSHOT_STRENGTH = register("slingshot_strength",
            ComponentType.<List<EnchantmentEffectEntry<EnchantmentValueEffect>>>builder().codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf()));
    public static ComponentType<EnchantmentValueEffect> PROJECTILE_BOUNCE = register("projectile_bounce", ComponentType.<EnchantmentValueEffect>builder().codec(EnchantmentValueEffect.CODEC));
    public static ComponentType<Unit> PROJECTILE_PREDICTION = register("projectile_prediction", ComponentType.<Unit>builder().codec(Unit.CODEC));
    public static ComponentType<Unit> PROJECTILE_BLOCK_PLACER = register("projectile_block_placer", ComponentType.<Unit>builder().codec(Unit.CODEC));
    public static ComponentType<Unit> PROJECTILE_TOOL_USER = register("projectile_tool_user", ComponentType.<Unit>builder().codec(Unit.CODEC));
    public static ComponentType<Unit> PROJECTILE_BOOMERANG_ENTITY = register("projectile_boomerang_entity", ComponentType.<Unit>builder().codec(Unit.CODEC));
    public static ComponentType<Unit> PROJECTILE_BOOMERANG_BLOCK = register("projectile_boomerang_block", ComponentType.<Unit>builder().codec(Unit.CODEC));
    public static ComponentType<Unit> PROJECTILE_FORCE_ITEM = register("projectile_force_item", ComponentType.<Unit>builder().codec(Unit.CODEC));
    public static ComponentType<Unit> PROJECTILE_ITEM_NO_SIDE_EFFECTS = register("projectile_item_no_side_effects", ComponentType.<Unit>builder().codec(Unit.CODEC));
    public static <T> ComponentType<T> register(String path, ComponentType.Builder<T> builder) {
        var x =  Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Identifier.of(ModInit.ID, path), builder.build());
        PolymerComponent.registerDataComponent(x);
        return x;
    }

    public static void register() {
    }
}
