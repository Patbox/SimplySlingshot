package eu.pb4.slingshot.item;

import eu.pb4.slingshot.ModInit;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SlingshotDataComponents {

    public static <T> ComponentType<T> register(String path, ComponentType.Builder<T> builder) {
        var x =  Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(ModInit.ID, path), builder.build());
        PolymerComponent.registerDataComponent(x);
        return x;
    }
}
