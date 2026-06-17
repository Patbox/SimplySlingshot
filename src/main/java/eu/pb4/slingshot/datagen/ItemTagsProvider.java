package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.SlingshotItemIds;
import eu.pb4.slingshot.item.SlingshotItemTags;
import eu.pb4.slingshot.item.SlingshotItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    public ItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, @Nullable FabricTagsProvider.BlockTagsProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(SlingshotItemIds.SLINGSHOT);
        this.tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(SlingshotItemIds.SLINGSHOT);

        this.tag(ConventionalItemTags.RANGED_WEAPON_TOOLS)
                .add(SlingshotItemIds.SLINGSHOT);

        this.tag(SlingshotItemTags.ALWAYS_USABLE_ITEMS)
                .addOptionalTag(ConventionalItemTags.DYES)
;
        this.tag(SlingshotItemTags.ALWAYS_BLOCK_USABLE_ITEMS)
                .addOptionalTag(SlingshotItemTags.ALWAYS_USABLE_ITEMS)
                .addOptionalTag(ConventionalItemTags.MUSIC_DISCS)
                .addOptionalTag(ConventionalItemTags.DYES)
                .addOptionalTag(ConventionalItemTags.SEEDS)
                .addOptionalTag(ConventionalItemTags.FLOWERS)
                .add(ItemIds.MINECART)
                .add(ItemIds.CHEST_MINECART)
                .add(ItemIds.FURNACE_MINECART)
                .add(ItemIds.HOPPER_MINECART)
                .add(ItemIds.TNT_MINECART)
                .add(ItemIds.END_CRYSTAL)
                .add(ItemIds.BONE_MEAL)
                .add(ItemIds.ENDER_EYE)
                .addOptionalTag(ItemTags.SAPLINGS)
                .addOptionalTag(ItemTags.BOATS)
                .addOptionalTag(ItemTags.CHEST_BOATS)
                .add(BlockItemIds.TORCH.item())
                .add(BlockItemIds.SOUL_TORCH.item())
                .add(BlockItemIds.REDSTONE_TORCH.item())
                .add(ItemIds.TRIAL_KEY)
                .add(ItemIds.OMINOUS_TRIAL_KEY)
                .add(ItemIds.ITEM_FRAME)
                .add(ItemIds.GLOW_ITEM_FRAME)
                .add(ItemIds.PAINTING)
                .add(ItemIds.HONEYCOMB)
        ;

        this.tag(SlingshotItemTags.ALWAYS_ENTITY_USABLE_ITEMS)
                .addOptionalTag(SlingshotItemTags.ALWAYS_USABLE_ITEMS)
                .addOptionalTag(ItemTags.FURNACE_MINECART_FUEL)
                .addOptionalTag(ItemTags.AXES)
                .add(ItemIds.IRON_INGOT)
        ;

        this.tag(SlingshotItemTags.ENCHANTMENT_USABLE_ITEMS)
                .addOptionalTag(ItemTags.SHOVELS)
                .addOptionalTag(ItemTags.AXES)
                .addOptionalTag(ItemTags.HOES);

        this.tag(SlingshotItemTags.BRICK_LIKE)
                .addOptionalTag(ConventionalItemTags.INGOTS)
                .addOptionalTag(ConventionalItemTags.BRICKS)
                .addOptionalTag(ConventionalItemTags.COBBLESTONES)
                .addOptionalTag(ConventionalItemTags.STONES)
                .addOptionalTag(ConventionalItemTags.TRIDENT_TOOLS)
                .addOptionalTag(ItemTags.PICKAXES)
                .addOptionalTag(ItemTags.SHOVELS)
                .addOptionalTag(ItemTags.AXES)
                .addOptionalTag(ItemTags.SWORDS)
                .addOptionalTag(ItemTags.SPEARS)
                .add(SlingshotItemIds.PEBBLE)
        ;

        this.tag(SlingshotItemTags.SLIME_LIKE)
                .addOptionalTag(ConventionalItemTags.SLIME_BALLS)
                .add(BlockItemIds.SLIME_BLOCK.item())
                .add(ItemIds.MAGMA_CREAM);

        this.tag(SlingshotItemTags.ROTATE_ON_Y_AXIS_45_DEG)
                .addOptionalTag(ConventionalItemTags.RODS)
                .addOptionalTag(ConventionalItemTags.TOOLS)
                .addOptionalTag(ConventionalItemTags.MACE_TOOLS)
                .add(ItemIds.AMETHYST_SHARD)
                .add(ItemIds.BONE)
        ;

        this.tag(SlingshotItemTags.ROTATE_ON_Y_AXIS_N45_DEG)
                .add(ItemIds.PRISMARINE_SHARD)
                .addOptionalTag(ItemTags.SPEARS)
                .add(ItemIds.ECHO_SHARD)
        ;

        this.tag(SlingshotItemTags.ROTATE_ON_Y_AXIS_180_DEG)
                .add(BlockItemIds.POINTED_DRIPSTONE.item())
        ;

        this.tag(SlingshotItemTags.ROTATE_ON_Y_AXIS)
                .add(BlockItemIds.END_ROD.item())
                .addOptionalTag(BlockItemTags.LIGHTNING_RODS.item())
                .addOptionalTag(SlingshotItemTags.ROTATE_ON_Y_AXIS_45_DEG)
                .addOptionalTag(SlingshotItemTags.ROTATE_ON_Y_AXIS_N45_DEG)
                .addOptionalTag(SlingshotItemTags.ROTATE_ON_Y_AXIS_180_DEG)
        ;

        this.tag(SlingshotItemTags.HIGH_PROJECTILE_DAMAGE)
                .addOptionalTag(ItemTags.ANVIL)
                .add(BlockItemIds.HEAVY_CORE.item())
                .add(ItemIds.NETHER_STAR)
        ;

        this.tag(SlingshotItemTags.MEDIUM_PROJECTILE_DAMAGE)
                .addOptionalTag(SlingshotItemTags.BRICK_LIKE)
                .addOptionalTag(ConventionalItemTags.RODS)
                .add(BlockItemIds.END_ROD.item())
                .addOptionalTag(BlockItemTags.LIGHTNING_RODS.item())
                .add(BlockItemIds.POINTED_DRIPSTONE.item())
                .add(ItemIds.AMETHYST_SHARD)
                .add(ItemIds.PRISMARINE_SHARD)
                .add(ItemIds.ECHO_SHARD)
                .addOptionalTag(ConventionalItemTags.OBSIDIANS);

        this.tag(SlingshotItemTags.LOW_PROJECTILE_DAMAGE)
                .addOptionalTag(SlingshotItemTags.SLIME_LIKE)
                .addOptionalTag(ItemTags.WOOL)
                .addOptionalTag(ItemTags.WOOL_CARPETS)
                .addOptionalTag(ConventionalItemTags.FEATHERS)
                .addOptionalTag(ConventionalItemTags.WHEAT_CROPS)
                .addOptionalTag(ConventionalItemTags.SEEDS)
                .addOptionalTag(ConventionalItemTags.FLOWERS)
                .addOptionalTag(ItemTags.SAPLINGS)
                .add(BlockItemIds.HAY_BLOCK.item())
        ;
    }
}
