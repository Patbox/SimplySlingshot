package eu.pb4.slingshot.datagen;

import com.google.common.hash.HashCode;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ConditionItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.RangeDispatchItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.UsingItemProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.numeric.UseDurationProperty;
import eu.pb4.slingshot.item.SlingshotItems;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.ConstantTintSource;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.DyeTintSource;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static eu.pb4.slingshot.ModInit.id;

class CustomAssetProvider implements DataProvider {
    private final PackOutput output;

    public CustomAssetProvider(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        BiConsumer<String, byte[]> assetWriter = (path, data) -> {
            try {
                writer.writeIfNeeded(this.output.getOutputFolder().resolve(path), data, HashCode.fromBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return CompletableFuture.runAsync(() -> {
            writeData(assetWriter);
        }, Util.backgroundExecutor());
    }

    private void writeData(BiConsumer<String, byte[]> writer) {
        writer.accept(AssetPaths.itemAsset(id("slingshot")), new ItemAsset(
                new ConditionItemModel(new UsingItemProperty(),
                        RangeDispatchItemModel.builder(new UseDurationProperty(false))
                                .scale(0.05f)
                                .fallback(new BasicItemModel(id("item/slingshot_pull_1")))
                                .entry(0.65f, new BasicItemModel(id("item/slingshot_pull_2")))
                                .entry(0.9f, new BasicItemModel(id("item/slingshot_pull_3")))
                                .build(),

                        new BasicItemModel(id("item/slingshot"))
                        ),
                ItemAsset.Properties.DEFAULT).toJson().getBytes(StandardCharsets.UTF_8));

        writer.accept(AssetPaths.itemAsset(id("pebble")), new ItemAsset(new BasicItemModel(id("item/pebble")),
                ItemAsset.Properties.DEFAULT).toJson().getBytes(StandardCharsets.UTF_8));
        writer.accept(AssetPaths.itemAsset(id("slinshot_pull_base")), new ItemAsset(new BasicItemModel(id("item/slingshot_pull_base")),
                ItemAsset.Properties.DEFAULT).toJson().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        return "slingshot:assets";
    }
}
