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

public class SlingshotItems {

    public static final SlingshotItem SLINGSHOT = register("slingshot", (settings) -> new SlingshotItem(
            settings.durability(340)
                    .enchantable(3)
                    .repairable(Items.STRING)
                    .component(SlingshotDataComponents.SLINGSHOT_WEAPON_DAMAGE, 1.25f)
                    .component(SlingshotDataComponents.SLINGSHOT_WEAPON_KNOCKBACK_BONUS, 0f)

    ));

    public static final Item PEBBLE = register("pebble", SimplePolymerItem::new);

    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.insertAfter(Items.CROSSBOW, SLINGSHOT);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.insertAfter(Items.FISHING_ROD, SLINGSHOT);
        });
    }

    public static <T extends Item> T register(String path, Item.Properties settings, Function<Item.Properties, T> function) {
        var id = Identifier.fromNamespaceAndPath(ModInit.ID, path);
        var item = function.apply(settings.setId(ResourceKey.create(Registries.ITEM, id)));
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return item;
    }

    public static <T extends Item> T register(String path, Function<Item.Properties, T> function) {
        return register(path, new Item.Properties(), function);
    }
}
