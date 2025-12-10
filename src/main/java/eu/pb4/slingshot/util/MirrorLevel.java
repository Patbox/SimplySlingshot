package eu.pb4.slingshot.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.Util;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.*;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class MirrorLevel extends Level {
    private final Level world;
    private final MirrorChunkManager chunkManager = new MirrorChunkManager();

    public MirrorLevel(Level world) {
        super((WritableLevelData) world.getLevelData(), world.dimension(), world.registryAccess(), world.dimensionTypeRegistration(), world.isClientSide(), world.isDebug(), 0, 0);
        this.world = world;
    }

    public static MirrorLevel get(Level world) {
        return ((Provider) world).slingshot$getMirror();
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public void setRespawnData(LevelData.RespawnData spawnPoint) {

    }

    @Override
    public LevelData.RespawnData getRespawnData() {
        return this.world.getRespawnData();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.world.getBlockState(pos);
    }

    @Override
    public boolean hasChunk(int chunkX, int chunkZ) {
        return this.world.hasChunk(chunkX, chunkZ);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.world.getFluidState(pos);
    }

    @Override
    public Holder<Biome> getBiome(BlockPos pos) {
        return this.world.getBiome(pos);
    }


    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

    }

    @Override
    public void playSeededSound(@Nullable Entity source, double x, double y, double z, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {
    }

    @Override
    public void playSeededSound(@Nullable Entity source, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {
    }

    @Override
    public void explode(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, ExplosionInteraction explosionSourceType, ParticleOptions smallParticle, ParticleOptions largeParticle, WeightedList<ExplosionParticleInfo> blockParticles, Holder<SoundEvent> soundEvent) {

    }

    @Override
    public String gatherChunkSourceStats() {
        return "Mirror!" + this.world.gatherChunkSourceStats();
    }

    @Nullable
    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @Override
    public Collection<EnderDragonPart> dragonParts() {
        return List.of();
    }

    @Override
    public TickRateManager tickRateManager() {
        return this.world.tickRateManager();
    }

    @Nullable
    @Override
    public MapItemSavedData getMapData(MapId id) {
        return null;
    }

    @Override
    public void destroyBlockProgress(int entityId, BlockPos pos, int progress) {
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    @Override
    public RecipeAccess recipeAccess() {
        return this.world.recipeAccess();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return new LevelEntityGetter<Entity>() {
            @Nullable
            @Override
            public Entity get(int id) {
                return null;
            }

            @Nullable
            @Override
            public Entity get(UUID uuid) {
                return null;
            }

            @Override
            public Iterable<Entity> getAll() {
                return List.of();
            }

            @Override
            public <U extends Entity> void get(EntityTypeTest<Entity, U> filter, AbortableIterationConsumer<U> consumer) {
            }

            @Override
            public void get(AABB box, Consumer<Entity> action) {
            }

            @Override
            public <U extends Entity> void get(EntityTypeTest<Entity, U> filter, AABB box, AbortableIterationConsumer<U> consumer) {
            }
        };
    }

    @Override
    public EnvironmentAttributeSystem environmentAttributes() {
        return this.world.environmentAttributes();
    }

    @Override
    public PotionBrewing potionBrewing() {
        return this.world.potionBrewing();
    }

    @Override
    public FuelValues fuelValues() {
        return this.world.fuelValues();
    }

    @Override
    public ChunkSource getChunkSource() {
        return this.chunkManager;
    }

    @Override
    public void levelEvent(@Nullable Entity source, int eventId, BlockPos pos, int data) {

    }

    @Override
    public void gameEvent(Holder<GameEvent> event, Vec3 emitterPos, GameEvent.Context emitter) {

    }

    @Override
    public float getShade(Direction direction, boolean shaded) {
        return 0;
    }

    @Override
    public List<? extends Player> players() {
        return List.of();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int biomeX, int biomeY, int biomeZ) {
        return this.world.getUncachedNoiseBiome(biomeX, biomeY, biomeZ);
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.world.enabledFeatures();
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return new LevelTickAccess<Block>() {
            @Override
            public boolean willTickThisTick(BlockPos blockPos, Block object) {
                return false;
            }

            @Override
            public void schedule(ScheduledTick<Block> orderedTick) {

            }

            @Override
            public boolean hasScheduledTick(BlockPos blockPos, Block object) {
                return false;
            }

            @Override
            public int count() {
                return 0;
            }
        };
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return new LevelTickAccess<Fluid>() {
            @Override
            public boolean willTickThisTick(BlockPos blockPos, Fluid object) {
                return false;
            }

            @Override
            public void schedule(ScheduledTick<Fluid> orderedTick) {

            }

            @Override
            public boolean hasScheduledTick(BlockPos blockPos, Fluid object) {
                return false;
            }

            @Override
            public int count() {
                return 0;
            }
        };
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    public interface Provider {
        MirrorLevel slingshot$getMirror();
    }

    private class MirrorChunk extends LevelChunk {
        private final ChunkAccess chunk;

        public MirrorChunk(ChunkAccess chunk) {
            super(MirrorLevel.this, chunk.getPos(), UpgradeData.EMPTY,
                    new LevelChunkTicks<>(), new LevelChunkTicks<>(), 0L,
                    Util.make(new LevelChunkSection[chunk.getSections().length], arr -> {
                        for (var i = 0; i < chunk.getSections().length; i++) {
                            arr[i] = new MirrorChunkSection(chunk, i);
                        }
                    }), (PostLoadProcessor)null, (BlendingData)null);
            this.chunk = chunk;
        }

        @Nullable
        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return null;
        }

        @Nullable
        @Override
        public BlockState setBlockState(BlockPos pos, BlockState state, int flags) {
            return this.chunk.getBlockState(pos);
        }

        @Override
        public void setHeightmap(Heightmap.Types type, long[] heightmap) {}

        @Override
        public void setBlockEntity(BlockEntity blockEntity) {}

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return this.chunk.getBlockState(pos);
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return this.chunk.getFluidState(pos);
        }

        @Override
        public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types type) {
            return this.chunk.getOrCreateHeightmapUnprimed(type);
        }


    }

    private class MirrorChunkSection extends LevelChunkSection {
        private final ChunkAccess chunk;
        private final int index;

        public MirrorChunkSection(ChunkAccess chunk, int i) {
            super(MirrorLevel.this.palettedContainerFactory());
            this.chunk = chunk;
            this.index = i;
        }


        @Override
        public BlockState getBlockState(int x, int y, int z) {
            return getRealSection().getBlockState(x, y, z);
        }

        @Override
        public FluidState getFluidState(int x, int y, int z) {
            return getRealSection().getFluidState(x, y, z);
        }

        @Override
        public BlockState setBlockState(int x, int y, int z, BlockState state) {
            return getRealSection().getBlockState(x, y, z);
        }

        @Override
        public BlockState setBlockState(int x, int y, int z, BlockState state, boolean lock) {
            return getRealSection().getBlockState(x, y, z);
        }

        @Override
        public boolean hasOnlyAir() {
            return getRealSection().hasOnlyAir();
        }

        @Override
        public void recalcBlockCounts() {

        }

        private LevelChunkSection getRealSection() {
            return this.chunk.getSections()[index];
        }
    }

    private class MirrorChunkManager extends ChunkSource {
        final MirrorChunk empty = new MirrorChunk(new EmptyLevelChunk(MirrorLevel.this, ChunkPos.ZERO, MirrorLevel.this.registryAccess().getOrThrow(Biomes.PLAINS)));

        @Nullable
        @Override
        public ChunkAccess getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
            var chunk = MirrorLevel.this.world.getChunkSource().getChunk(x, z, leastStatus, false);
            return chunk != null ? new MirrorChunk(chunk) : (create ? empty : null);
        }

        @Override
        public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {

        }

        @Override
        public String gatherStats() {
            return "";
        }

        @Override
        public int getLoadedChunksCount() {
            return MirrorLevel.this.world.getChunkSource().getLoadedChunksCount();
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return MirrorLevel.this.world.getLightEngine();
        }

        @Override
        public BlockGetter getLevel() {
            return MirrorLevel.this;
        }
    }
}
