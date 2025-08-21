package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.SlingshotDataComponentTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

class DataComponentTagsProvider extends FabricTagProvider.FabricValueLookupTagProvider<ComponentType<?>> {
    public DataComponentTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DATA_COMPONENT_TYPE, registriesFuture, x -> Registries.DATA_COMPONENT_TYPE.getKey(x).orElseThrow());
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.valueLookupBuilder(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
        ;
        this.valueLookupBuilder(SlingshotDataComponentTags.ALWAYS_BLOCK_USABLE_ITEMS)
                .addOptionalTag(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
                .add(DataComponentTypes.JUKEBOX_PLAYABLE)
        ;

        this.valueLookupBuilder(SlingshotDataComponentTags.ALWAYS_ENTITY_USABLE_ITEMS)
                .addOptionalTag(SlingshotDataComponentTags.ALWAYS_USABLE_ITEMS)
        ;
    }
}
