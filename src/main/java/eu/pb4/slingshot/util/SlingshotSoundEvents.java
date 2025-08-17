package eu.pb4.slingshot.util;

import eu.pb4.polymer.core.api.other.PolymerSoundEvent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

import static eu.pb4.slingshot.ModInit.id;

public class SlingshotSoundEvents {
    public static final SoundEvent ENTITY_SLINGSHOT_SHOOT = of("entity.slingshot.shoot");
    public static final SoundEvent ENTITY_SLINGSHOT_LOAD = of("entity.slingshot.load");


    public static SoundEvent of(String path) {
        return Registry.register(Registries.SOUND_EVENT, id(path), PolymerSoundEvent.registerOverlay(SoundEvent.of(id(path))));
    }

    public static void register() {
    }
}
