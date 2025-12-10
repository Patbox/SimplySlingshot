package eu.pb4.slingshot.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.slingshot.ModInit;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.addAfter(Items.CROSSBOW, SLINGSHOT);
            entries.addAfter(Items.TIPPED_ARROW, PEBBLE);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.addBefore(Items.FISHING_ROD, SLINGSHOT);
        });



        /*PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(ModInit.ID, "a_group"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(HANG_GLIDER::getDefaultStack)
                .displayName(Text.translatable("itemgroup." + ModInit.ID))
                .entries(((context, entries) -> {
                    entries.add(HANG_GLIDER);
                    for (var color : DyeColor.values()) {
                        if (color != DyeColor.WHITE) {
                            var glider = HANG_GLIDER.getDefaultStack();
                            glider.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color.getEntityColor()));
                            entries.add(glider);
                        }
                    }
                    entries.add(CHERRY_HANG_GLIDER);
                    entries.add(SCULK_HANG_GLIDER);
                    entries.add(AZALEA_HANG_GLIDER);
                    entries.add(PHANTOM_HANG_GLIDER);

                    entries.add(WIND_IN_A_BOTTLE);
                    entries.add(INFINITE_WIND_IN_A_BOTTLE);
                })).build()
        );*/
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
