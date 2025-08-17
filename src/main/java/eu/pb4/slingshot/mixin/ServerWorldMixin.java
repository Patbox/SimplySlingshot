package eu.pb4.slingshot.mixin;

import eu.pb4.slingshot.util.MirrorWorld;
import eu.pb4.slingshot.util.ServerWorldExt;
import eu.pb4.slingshot.util.TimedMiningProgress;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements MirrorWorld.Provider, ServerWorldExt {
    @Unique
    private final MirrorWorld mirrorWorld = new MirrorWorld(this);

    @Unique
    private final Map<BlockPos, TimedMiningProgress> miningProgressMap = new HashMap<>();

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void clearOldSlingshotMining(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.miningProgressMap.values().removeIf(x -> x.time() + 30 * 20 < this.getTime());
    }

    @Override
    public MirrorWorld slingshot$getMirror() {
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
