package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.ench.SlingshotEnchantmentTags;
import eu.pb4.slingshot.item.ench.SlingshotEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.concurrent.CompletableFuture;

class EnchantmentTagsProvider extends FabricTagProvider<Enchantment> {
    public EnchantmentTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENCHANTMENT, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.getOrCreateRawBuilder(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addOptionalElement(SlingshotEnchantments.BOUNCE.identifier())
                .addOptionalElement(SlingshotEnchantments.BLOCK_PLACER.identifier())
                .addOptionalElement(SlingshotEnchantments.BOOMERANG.identifier())
                .addOptionalElement(SlingshotEnchantments.TRAJECTORY_PREDICTION.identifier())
                .addOptionalElement(SlingshotEnchantments.ITEM_SENDER.identifier())
                .addOptionalElement(SlingshotEnchantments.TOOL_USER.identifier());

        this.getOrCreateRawBuilder(EnchantmentTags.TRADEABLE)
                .addOptionalElement(SlingshotEnchantments.BOUNCE.identifier())
                .addOptionalElement(SlingshotEnchantments.BLOCK_PLACER.identifier())
                .addOptionalElement(SlingshotEnchantments.BOOMERANG.identifier())
                .addOptionalElement(SlingshotEnchantments.TRAJECTORY_PREDICTION.identifier())
                .addOptionalElement(SlingshotEnchantments.ITEM_SENDER.identifier())
                .addOptionalElement(SlingshotEnchantments.TOOL_USER.identifier());

        this.getOrCreateRawBuilder(EnchantmentTags.ON_RANDOM_LOOT)
                .addOptionalElement(SlingshotEnchantments.BOUNCE.identifier())
                .addOptionalElement(SlingshotEnchantments.BLOCK_PLACER.identifier())
                .addOptionalElement(SlingshotEnchantments.BOOMERANG.identifier())
                .addOptionalElement(SlingshotEnchantments.TRAJECTORY_PREDICTION.identifier())
                .addOptionalElement(SlingshotEnchantments.ITEM_SENDER.identifier())
                .addOptionalElement(SlingshotEnchantments.TOOL_USER.identifier());

        this.getOrCreateRawBuilder(SlingshotEnchantmentTags.ITEM_SENDER_INCOMPATIBLE)
                .addOptionalElement(SlingshotEnchantments.BLOCK_PLACER.identifier())
                .addOptionalElement(SlingshotEnchantments.BOOMERANG.identifier())
                .addOptionalElement(SlingshotEnchantments.TOOL_USER.identifier());
    }
}
