package eu.pb4.slingshot.datagen;

import eu.pb4.slingshot.item.SlingshotItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class RecipesProvider extends FabricRecipeProvider {
    public RecipesProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, BootstrapContext<Recipe<?>> bootstrapContext, BootstrapContext<Advancement> bootstrapContext1) {
        return new RecipeProvider(bootstrapContext, bootstrapContext1) {
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
