package eu.pb4.slingshot.util;

import net.minecraft.util.math.BlockPos;

public interface ServerWorldExt {
    TimedMiningProgress slingshot$getBreakingProgress(BlockPos pos);
    void slingshot$setBreakingProgress(BlockPos pos, TimedMiningProgress progress);
}
