package eu.pb4.slingshot.util;

import eu.pb4.slingshot.entity.Bounceable;
import net.minecraft.world.phys.BlockHitResult;

public interface BounceableExt extends Bounceable {
    void slingshot$setBounces(int bounces);
    int slingshot$getBounces();

    @Override
    default void onBouncedOff(BlockHitResult result) {};
}
