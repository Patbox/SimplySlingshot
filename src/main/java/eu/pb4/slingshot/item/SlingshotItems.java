package eu.pb4.slingshot.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.slingshot.ModInit;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class SlingshotItems {

    public static final SlingshotItem SLINGSHOT = register("slingshot", (settings) -> new SlingshotItem(
            settings.maxDamage(340)
                    .enchantable(3)
                    .repairable(Items.STRING)
                    .component(SlingshotDataComponents.SLINGSHOT_WEAPON_DAMAGE, 1.25f)
                    .component(SlingshotDataComponents.SLINGSHOT_WEAPON_KNOCKBACK_BONUS, 0f)

    ));

    public static final Item PEBBLE = register("pebble", SimplePolymerItem::new);

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.CROSSBOW, SLINGSHOT);
            entries.addAfter(Items.TIPPED_ARROW, PEBBLE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
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

    public static <T extends Item> T register(String path, Item.Settings settings, Function<Item.Settings, T> function) {
        var id = Identifier.of(ModInit.ID, path);
        var item = function.apply(settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
        Registry.register(Registries.ITEM, id, item);
        return item;
    }

    public static <T extends Item> T register(String path, Function<Item.Settings, T> function) {
        return register(path, new Item.Settings(), function);
    }
}
