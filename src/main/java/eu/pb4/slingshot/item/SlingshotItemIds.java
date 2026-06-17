package eu.pb4.slingshot.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.slingshot.ModInit;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class SlingshotItemIds {

    public static final ResourceKey<Item> SLINGSHOT = register("slingshot");

    public static final ResourceKey<Item> PEBBLE = register("pebble");


    public static ResourceKey<Item> register(String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ModInit.ID, path));
    }
}
