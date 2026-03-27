package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.entity.SlingshotEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import java.util.concurrent.CompletableFuture;

class EntityTagsProvider extends FabricTagsProvider.EntityTypeTagsProvider {
    public EntityTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.valueLookupBuilder(EntityTypeTags.IMPACT_PROJECTILES).add(SlingshotEntities.ITEM_PROJECTILE);
    }
}
