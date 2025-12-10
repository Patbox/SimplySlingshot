package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.SlingshotItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


class AdvancementsProvider extends FabricAdvancementProvider {

    protected AdvancementsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> exporter) {
        var item = registryLookup.lookupOrThrow(Registries.ITEM);
        //noinspection removal
        /*var root = Advancement.Builder.create()
                .display(
                        SlingshotItems.HANG_GLIDER,
                        Text.translatable("advancements.glideaway.into_the_skies.title"),
                        Text.translatable("advancements.glideaway.into_the_skies.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .parent(Identifier.ofVanilla("adventure/root"))
                .criterion("any_item", GliderEntity.FLY_WITH_GLIDER.create(new TravelCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(DistancePredicate.absolute(NumberRange.DoubleRange.atLeast(5))))))
                .build(exporter, "glideaway:into_the_skies");*/

    }
}
