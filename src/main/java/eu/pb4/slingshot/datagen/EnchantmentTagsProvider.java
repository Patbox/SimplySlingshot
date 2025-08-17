package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.ench.SlingshotEnchantmentTags;
import eu.pb4.slingshot.item.ench.SlingshotEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

class EnchantmentTagsProvider extends FabricTagProvider<Enchantment> {
    public EnchantmentTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ENCHANTMENT, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.getTagBuilder(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addOptional(SlingshotEnchantments.BOUNCE.getValue())
                .addOptional(SlingshotEnchantments.BLOCK_PLACER.getValue())
                .addOptional(SlingshotEnchantments.BOOMERANG.getValue())
                .addOptional(SlingshotEnchantments.TRAJECTORY_PREDICTION.getValue())
                .addOptional(SlingshotEnchantments.ITEM_SENDER.getValue())
                .addOptional(SlingshotEnchantments.TOOL_USER.getValue());

        this.getTagBuilder(EnchantmentTags.TRADEABLE)
                .addOptional(SlingshotEnchantments.BOUNCE.getValue())
                .addOptional(SlingshotEnchantments.BLOCK_PLACER.getValue())
                .addOptional(SlingshotEnchantments.BOOMERANG.getValue())
                .addOptional(SlingshotEnchantments.TRAJECTORY_PREDICTION.getValue())
                .addOptional(SlingshotEnchantments.ITEM_SENDER.getValue())
                .addOptional(SlingshotEnchantments.TOOL_USER.getValue());

        this.getTagBuilder(EnchantmentTags.ON_RANDOM_LOOT)
                .addOptional(SlingshotEnchantments.BOUNCE.getValue())
                .addOptional(SlingshotEnchantments.BLOCK_PLACER.getValue())
                .addOptional(SlingshotEnchantments.BOOMERANG.getValue())
                .addOptional(SlingshotEnchantments.TRAJECTORY_PREDICTION.getValue())
                .addOptional(SlingshotEnchantments.ITEM_SENDER.getValue())
                .addOptional(SlingshotEnchantments.TOOL_USER.getValue());

        this.getTagBuilder(SlingshotEnchantmentTags.ITEM_SENDER_INCOMPATIBLE)
                .addOptional(SlingshotEnchantments.BLOCK_PLACER.getValue())
                .addOptional(SlingshotEnchantments.BOOMERANG.getValue())
                .addOptional(SlingshotEnchantments.TOOL_USER.getValue());
    }
}
