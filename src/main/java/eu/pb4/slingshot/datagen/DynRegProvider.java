package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.entity.SlingshotEntities;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentComponents;
import eu.pb4.slingshot.item.ench.SlingshotEnchantmentTags;
import eu.pb4.slingshot.item.ench.SlingshotEnchantments;
import eu.pb4.slingshot.item.SlingshotItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class DynRegProvider extends FabricDynamicRegistryProvider {
    public DynRegProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider wrapperLookup, Entries entries) {
        var slingshot = HolderSet.direct(BuiltInRegistries.ITEM::wrapAsHolder, SlingshotItems.SLINGSHOT);

        add(entries, SlingshotEnchantments.BOUNCE, Enchantment.enchantment(
                new Enchantment.EnchantmentDefinition(slingshot, Optional.empty(), 2, 5,
                        Enchantment.dynamicCost(8, 8), Enchantment.constantCost(50), 6, List.of(EquipmentSlotGroup.HAND))
        ).withSpecialEffect(SlingshotEnchantmentComponents.PROJECTILE_BOUNCE, new AddValue(LevelBasedValue.perLevel(1))));

        add(entries, SlingshotEnchantments.BLOCK_PLACER, Enchantment.enchantment(
                new Enchantment.EnchantmentDefinition(slingshot, Optional.empty(), 4, 1,
                        Enchantment.constantCost(28), Enchantment.constantCost(50), 8, List.of(EquipmentSlotGroup.HAND))
        ).withSpecialEffect(SlingshotEnchantmentComponents.PROJECTILE_BLOCK_PLACER, Unit.INSTANCE));

        add(entries, SlingshotEnchantments.TOOL_USER, Enchantment.enchantment(
                new Enchantment.EnchantmentDefinition(slingshot, Optional.empty(), 3, 1,
                        Enchantment.constantCost(16), Enchantment.constantCost(50), 8, List.of(EquipmentSlotGroup.HAND)))
                .withSpecialEffect(SlingshotEnchantmentComponents.PROJECTILE_TOOL_USER, Unit.INSTANCE));

        add(entries, SlingshotEnchantments.TRAJECTORY_PREDICTION, Enchantment.enchantment(
                new Enchantment.EnchantmentDefinition(slingshot, Optional.empty(), 5, 1,
                        Enchantment.constantCost(8), Enchantment.constantCost(50), 5, List.of(EquipmentSlotGroup.HAND)))
                .withSpecialEffect(SlingshotEnchantmentComponents.PROJECTILE_PREDICTION, Unit.INSTANCE));

        add(entries, SlingshotEnchantments.BOOMERANG, Enchantment.enchantment(
                new Enchantment.EnchantmentDefinition(slingshot, Optional.empty(), 2, 1,
                        Enchantment.constantCost(25), Enchantment.constantCost(50), 16, List.of(EquipmentSlotGroup.HAND)))
                .withSpecialEffect(SlingshotEnchantmentComponents.PROJECTILE_BOOMERANG_BLOCK, Unit.INSTANCE));

        add(entries, SlingshotEnchantments.ITEM_SENDER, Enchantment.enchantment(
                        new Enchantment.EnchantmentDefinition(slingshot, Optional.empty(), 3, 1,
                                Enchantment.constantCost(18), Enchantment.constantCost(50), 16, List.of(EquipmentSlotGroup.HAND)))
                .withSpecialEffect(SlingshotEnchantmentComponents.PROJECTILE_FORCE_ITEM, Unit.INSTANCE)
                .withSpecialEffect(SlingshotEnchantmentComponents.PROJECTILE_ITEM_NO_SIDE_EFFECTS, Unit.INSTANCE)
                .withEffect(EnchantmentEffectComponents.DAMAGE, new SetValue(LevelBasedValue.constant(0)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(wrapperLookup.lookupOrThrow(Registries.ENTITY_TYPE), SlingshotEntities.ITEM_PROJECTILE).build()))
                .withEffect(SlingshotEnchantmentComponents.SLINGSHOT_STRENGTH, new AddValue(new LevelBasedValue.Constant(0.5f)))
                .exclusiveWith(wrapperLookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SlingshotEnchantmentTags.ITEM_SENDER_INCOMPATIBLE))
        );
    }

    private Holder<Enchantment> add(Entries entries, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        return entries.add(key, builder.build(key.identifier()));
    }

    @Override
    public String getName() {
        return "dynreg";
    }
}
