package eu.pb4.slingshot.entity;

import net.minecraft.world.phys.BlockHitResult;

public interface Bounceable {
    void onBouncedOff(BlockHitResult result);
}
