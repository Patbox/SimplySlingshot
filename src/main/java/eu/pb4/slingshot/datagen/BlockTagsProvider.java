package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.block.SlingshotBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

class BlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public BlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.valueLookupBuilder(SlingshotBlockTags.BRICK_BREAKABLE)
                .add(Blocks.FLOWER_POT)
                .add(Blocks.DECORATED_POT)
                .addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS)
                .addOptionalTag(ConventionalBlockTags.GLASS_PANES);
    }
}
