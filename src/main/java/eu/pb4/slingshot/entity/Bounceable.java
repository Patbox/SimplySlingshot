package eu.pb4.slingshot.entity;

import net.minecraft.util.hit.BlockHitResult;

public interface Bounceable {
    void onBouncedOff(BlockHitResult result);
}
