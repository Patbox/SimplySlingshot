package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.SlingshotItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class RecipesProvider extends FabricRecipeProvider {
    public RecipesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                //noinspection unchecked
                shaped(RecipeCategory.COMBAT, SlingshotItems.SLINGSHOT)
                        .pattern("sls")
                        .pattern("tit")
                        .pattern(" i ")
                        .define('s', Items.STRING)
                        .define('l', Items.LEATHER)
                        .define('t', Items.TRIPWIRE_HOOK)
                        .define('i', Items.STICK)
                        .unlockedBy("item_get", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STRING, Items.LEATHER, Items.TRIPWIRE_HOOK))
                        .save(output);
            }

            public void of(RecipeOutput exporter, RecipeHolder<?>... recipes) {
                for (var recipe : recipes) {
                    exporter.accept(recipe.id(), recipe.value(), null);
                }
            }
        };
    }

    @Override
    public String getName() {
        return "recipe";
    }
}
