package eu.pb4.slingshot.mixin;

import eu.pb4.slingshot.util.MirrorLevel;
import eu.pb4.slingshot.util.ServerWorldExt;
import eu.pb4.slingshot.util.TimedMiningProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements MirrorLevel.Provider, ServerWorldExt {
    @Unique
    private final MirrorLevel mirrorWorld = new MirrorLevel(this);

    @Unique
    private final Map<BlockPos, TimedMiningProgress> miningProgressMap = new HashMap<>();

    protected ServerLevelMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void clearOldSlingshotMining(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.miningProgressMap.values().removeIf(x -> x.time() + 30 * 20 < this.getGameTime());
    }

    @Override
    public MirrorLevel slingshot$getMirror() {
        return this.mirrorWorld;
    }

    @Override
    public TimedMiningProgress slingshot$getBreakingProgress(BlockPos pos) {
        return this.miningProgressMap.get(pos);
    }

    @Override
    public void slingshot$setBreakingProgress(BlockPos pos, TimedMiningProgress progress) {
        if (progress == null) {
            this.miningProgressMap.remove(pos);
            return;
        }
        this.miningProgressMap.put(pos, progress);
    }
}
