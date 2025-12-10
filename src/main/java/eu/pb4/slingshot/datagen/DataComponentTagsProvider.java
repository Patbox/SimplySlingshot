package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.SlingshotDataComponentTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import java.util.concurrent.CompletableFuture;

class DataComponentTagsProvider extends FabricTagProvider.FabricValueLookupTagProvider<DataComponentType<?>> {
    public DataComponentTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DATA_COMPONENT_TYPE, registriesFuture, x -> BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(x).orElseThrow());
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.valueLookupBuilder(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
        ;
        this.valueLookupBuilder(SlingshotDataComponentTags.ALWAYS_BLOCK_USABLE_ITEMS)
                .addOptionalTag(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
                .add(DataComponents.JUKEBOX_PLAYABLE)
        ;

        this.valueLookupBuilder(SlingshotDataComponentTags.ALWAYS_ENTITY_USABLE_ITEMS)
                .addOptionalTag(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
        ;
    }
}
