package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.SlingshotDataComponentTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxPlayable;

import java.util.concurrent.CompletableFuture;

class DataComponentTagsProvider extends FabricTagsProvider<DataComponentType<?>> {
    public DataComponentTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DATA_COMPONENT_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
        ;
        this.tag(SlingshotDataComponentTags.ALWAYS_BLOCK_USABLE_ITEMS)
                .addOptionalTag(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
                .add(key(DataComponents.JUKEBOX_PLAYABLE))
        ;

        this.tag(SlingshotDataComponentTags.ALWAYS_ENTITY_USABLE_ITEMS)
                .addOptionalTag(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
        ;
    }

    private ResourceKey<DataComponentType<?>> key(DataComponentType<?> comp) {
        return BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(comp).orElseThrow();
    }
}
