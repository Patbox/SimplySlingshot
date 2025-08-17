package eu.pb4.slingshot.util;

import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class FakeChunkManager extends ChunkManager {
    private final MirrorWorld world;

    public FakeChunkManager(MirrorWorld world) {
        this.world = world;
    }
    @Nullable
    @Override
    public Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        return null;
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {

    }

    @Override
    public String getDebugString() {
        return "";
    }

    @Override
    public int getLoadedChunkCount() {
        return this.world.getChunkManager().getLoadedChunkCount();
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    public BlockView getWorld() {
        return this.world;
    }
}
