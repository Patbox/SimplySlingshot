package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.entity.SlingshotEntities;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentComponents;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentTags;
import eu.pb4.slingshot.item.ench.SlingshotEnchantments;
import eu.pb4.slingshot.item.SlingshotItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.enchantment.effect.value.SetEnchantmentEffect;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.Unit;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class DynRegProvider extends FabricDynamicRegistryProvider {
    public DynRegProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        var slingshot = RegistryEntryList.of(Registries.ITEM::getEntry, SlingshotItems.SLINGSHOT);

        add(entries, SlingshotEnchantments.BOUNCE, Enchantment.builder(
                new Enchantment.Definition(slingshot, Optional.empty(), 2, 5,
                        Enchantment.leveledCost(8, 8), Enchantment.constantCost(50), 6, List.of(AttributeModifierSlot.HAND))
        ).addNonListEffect(SlingshotEnchantmentComponents.PROJECTILE_BOUNCE, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1))));

        add(entries, SlingshotEnchantments.BLOCK_PLACER, Enchantment.builder(
                new Enchantment.Definition(slingshot, Optional.empty(), 4, 1,
                        Enchantment.constantCost(28), Enchantment.constantCost(50), 8, List.of(AttributeModifierSlot.HAND))
        ).addNonListEffect(SlingshotEnchantmentComponents.PROJECTILE_BLOCK_PLACER, Unit.INSTANCE));

        add(entries, SlingshotEnchantments.TOOL_USER, Enchantment.builder(
                new Enchantment.Definition(slingshot, Optional.empty(), 3, 1,
                        Enchantment.constantCost(16), Enchantment.constantCost(50), 8, List.of(AttributeModifierSlot.HAND)))
                .addNonListEffect(SlingshotEnchantmentComponents.PROJECTILE_TOOL_USER, Unit.INSTANCE));

        add(entries, SlingshotEnchantments.TRAJECTORY_PREDICTION, Enchantment.builder(
                new Enchantment.Definition(slingshot, Optional.empty(), 5, 1,
                        Enchantment.constantCost(8), Enchantment.constantCost(50), 5, List.of(AttributeModifierSlot.HAND)))
                .addNonListEffect(SlingshotEnchantmentComponents.PROJECTILE_PREDICTION, Unit.INSTANCE));

        add(entries, SlingshotEnchantments.BOOMERANG, Enchantment.builder(
                new Enchantment.Definition(slingshot, Optional.empty(), 2, 1,
                        Enchantment.constantCost(25), Enchantment.constantCost(50), 16, List.of(AttributeModifierSlot.HAND)))
                .addNonListEffect(SlingshotEnchantmentComponents.PROJECTILE_BOOMERANG_BLOCK, Unit.INSTANCE));

        add(entries, SlingshotEnchantments.ITEM_SENDER, Enchantment.builder(
                        new Enchantment.Definition(slingshot, Optional.empty(), 3, 1,
                                Enchantment.constantCost(18), Enchantment.constantCost(50), 16, List.of(AttributeModifierSlot.HAND)))
                .addNonListEffect(SlingshotEnchantmentComponents.PROJECTILE_FORCE_ITEM, Unit.INSTANCE)
                .addNonListEffect(SlingshotEnchantmentComponents.PROJECTILE_ITEM_NO_SIDE_EFFECTS, Unit.INSTANCE)
                .addEffect(EnchantmentEffectComponentTypes.DAMAGE, new SetEnchantmentEffect(EnchantmentLevelBasedValue.constant(0)),
                        EntityPropertiesLootCondition.builder(LootContext.EntityReference.DIRECT_ATTACKER,
                                EntityPredicate.Builder.create().type(wrapperLookup.getOrThrow(RegistryKeys.ENTITY_TYPE), SlingshotEntities.ITEM_PROJECTILE).build()))
                .addEffect(SlingshotEnchantmentComponents.SLINGSHOT_STRENGTH, new AddEnchantmentEffect(new EnchantmentLevelBasedValue.Constant(0.5f)))
                .exclusiveSet(wrapperLookup.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(SlingshotEnchantmentTags.ITEM_SENDER_INCOMPATIBLE))
        );
    }

    private RegistryEntry<Enchantment> add(Entries entries, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        return entries.add(key, builder.build(key.getValue()));
    }

    @Override
    public String getName() {
        return "dynreg";
    }
}
