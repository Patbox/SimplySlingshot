package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.SlingshotItemTags;
import eu.pb4.slingshot.item.SlingshotItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.valueLookupBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(SlingshotItems.SLINGSHOT);
        this.valueLookupBuilder(ItemTags.VANISHING_ENCHANTABLE)
                .add(SlingshotItems.SLINGSHOT);

        this.valueLookupBuilder(ConventionalItemTags.RANGED_WEAPON_TOOLS)
                .add(SlingshotItems.SLINGSHOT);

        this.valueLookupBuilder(SlingshotItemTags.ALWAYS_USABLE_ITEMS)
                .addOptionalTag(ConventionalItemTags.DYES)
;
        this.valueLookupBuilder(SlingshotItemTags.ALWAYS_BLOCK_USABLE_ITEMS)
                .addOptionalTag(SlingshotItemTags.ALWAYS_USABLE_ITEMS)
                .addOptionalTag(ConventionalItemTags.MUSIC_DISCS)
                .addOptionalTag(ConventionalItemTags.DYES)
                .addOptionalTag(ConventionalItemTags.SEEDS)
                .addOptionalTag(ConventionalItemTags.FLOWERS)
                .add(Items.MINECART)
                .add(Items.CHEST_MINECART)
                .add(Items.FURNACE_MINECART)
                .add(Items.HOPPER_MINECART)
                .add(Items.TNT_MINECART)
                .add(Items.END_CRYSTAL)
                .add(Items.BONE_MEAL)
                .add(Items.ENDER_EYE)
                .addOptionalTag(ItemTags.SAPLINGS)
                .addOptionalTag(ItemTags.BOATS)
                .addOptionalTag(ItemTags.CHEST_BOATS)
                .add(Items.TORCH)
                .add(Items.SOUL_TORCH)
                .add(Items.REDSTONE_TORCH)
                .add(Items.TRIAL_KEY)
                .add(Items.OMINOUS_TRIAL_KEY)
                .add(Items.ITEM_FRAME)
                .add(Items.GLOW_ITEM_FRAME)
                .add(Items.PAINTING)
                .add(Items.HONEYCOMB)
        ;

        this.valueLookupBuilder(SlingshotItemTags.ALWAYS_ENTITY_USABLE_ITEMS)
                .addOptionalTag(SlingshotItemTags.ALWAYS_USABLE_ITEMS)
                .addOptionalTag(ItemTags.FURNACE_MINECART_FUEL)
                .addOptionalTag(ItemTags.AXES)
                .add(Items.IRON_INGOT)
        ;

        this.valueLookupBuilder(SlingshotItemTags.ENCHANTMENT_USABLE_ITEMS)
                .addOptionalTag(ItemTags.SHOVELS)
                .addOptionalTag(ItemTags.AXES)
                .addOptionalTag(ItemTags.HOES);

        this.valueLookupBuilder(SlingshotItemTags.BRICK_LIKE)
                .addOptionalTag(ConventionalItemTags.INGOTS)
                .addOptionalTag(ConventionalItemTags.BRICKS)
                .addOptionalTag(ConventionalItemTags.COBBLESTONES)
                .addOptionalTag(ConventionalItemTags.STONES)
                .addOptionalTag(ConventionalItemTags.SPEAR_TOOLS)
                .addOptionalTag(ItemTags.PICKAXES)
                .addOptionalTag(ItemTags.SHOVELS)
                .addOptionalTag(ItemTags.AXES)
                .addOptionalTag(ItemTags.SWORDS)
                .add(SlingshotItems.PEBBLE)
        ;

        this.valueLookupBuilder(SlingshotItemTags.SLIME_LIKE)
                .addOptionalTag(ConventionalItemTags.SLIME_BALLS)
                .add(Items.SLIME_BLOCK)
                .add(Items.MAGMA_CREAM);

        this.valueLookupBuilder(SlingshotItemTags.ROTATE_ON_Y_AXIS_45_DEG)
                .addOptionalTag(ConventionalItemTags.RODS)
                .addOptionalTag(ConventionalItemTags.TOOLS)
                .addOptionalTag(ConventionalItemTags.MACE_TOOLS)
                .add(Items.AMETHYST_SHARD)
                .add(Items.BONE)
        ;

        this.valueLookupBuilder(SlingshotItemTags.ROTATE_ON_Y_AXIS_N45_DEG)
                .add(Items.PRISMARINE_SHARD)
                .add(Items.ECHO_SHARD)
        ;

        this.valueLookupBuilder(SlingshotItemTags.ROTATE_ON_Y_AXIS_180_DEG)
                .add(Items.POINTED_DRIPSTONE)
        ;

        this.valueLookupBuilder(SlingshotItemTags.ROTATE_ON_Y_AXIS)
                .add(Items.END_ROD)
                .add(Items.LIGHTNING_ROD)
                .addOptionalTag(SlingshotItemTags.ROTATE_ON_Y_AXIS_45_DEG)
                .addOptionalTag(SlingshotItemTags.ROTATE_ON_Y_AXIS_N45_DEG)
                .addOptionalTag(SlingshotItemTags.ROTATE_ON_Y_AXIS_180_DEG)
        ;

        this.valueLookupBuilder(SlingshotItemTags.HIGH_PROJECTILE_DAMAGE)
                .addOptionalTag(ItemTags.ANVIL)
                .add(Items.HEAVY_CORE)
                .add(Items.NETHER_STAR)
        ;

        this.valueLookupBuilder(SlingshotItemTags.MEDIUM_PROJECTILE_DAMAGE)
                .addOptionalTag(SlingshotItemTags.BRICK_LIKE)
                .addOptionalTag(ConventionalItemTags.RODS)
                .add(Items.END_ROD)
                .add(Items.LIGHTNING_ROD)
                .add(Items.POINTED_DRIPSTONE)
                .add(Items.AMETHYST_SHARD)
                .add(Items.PRISMARINE_SHARD)
                .add(Items.ECHO_SHARD)
                .addOptionalTag(ConventionalItemTags.OBSIDIANS);

        this.valueLookupBuilder(SlingshotItemTags.LOW_PROJECTILE_DAMAGE)
                .addOptionalTag(SlingshotItemTags.SLIME_LIKE)
                .addOptionalTag(ItemTags.WOOL)
                .addOptionalTag(ItemTags.WOOL_CARPETS)
                .addOptionalTag(ConventionalItemTags.FEATHERS)
                .addOptionalTag(ConventionalItemTags.WHEAT_CROPS)
                .addOptionalTag(ConventionalItemTags.SEEDS)
                .addOptionalTag(ConventionalItemTags.FLOWERS)
                .addOptionalTag(ItemTags.SAPLINGS)
                .add(Items.HAY_BLOCK)
        ;
    }
}
