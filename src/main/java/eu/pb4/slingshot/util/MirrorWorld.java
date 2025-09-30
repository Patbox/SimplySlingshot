package eu.pb4.slingshot.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.map.MapState;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class MirrorWorld extends World {
    private final World world;
    private final MirrorChunkManager chunkManager = new MirrorChunkManager();

    public MirrorWorld(World world) {
        super((MutableWorldProperties) world.getLevelProperties(), world.getRegistryKey(), world.getRegistryManager(), world.getDimensionEntry(), world.isClient(), world.isDebugWorld(), 0, 0);
        this.world = world;
    }

    public static MirrorWorld get(World world) {
        return ((Provider) world).slingshot$getMirror();
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public void setSpawnPoint(WorldProperties.SpawnPoint spawnPoint) {

    }

    @Override
    public WorldProperties.SpawnPoint getSpawnPoint() {
        return this.world.getSpawnPoint();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.world.getBlockState(pos);
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return this.world.isChunkLoaded(chunkX, chunkZ);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.world.getFluidState(pos);
    }

    @Override
    public RegistryEntry<Biome> getBiome(BlockPos pos) {
        return this.world.getBiome(pos);
    }


    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

    }

    @Override
    public void playSound(@Nullable Entity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {
    }

    @Override
    public void playSoundFromEntity(@Nullable Entity source, Entity entity, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {
    }

    @Override
    public void createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, Pool<BlockParticleEffect> blockParticles, RegistryEntry<SoundEvent> soundEvent) {

    }

    @Override
    public String asString() {
        return "Mirror!" + this.world.asString();
    }

    @Nullable
    @Override
    public Entity getEntityById(int id) {
        return null;
    }

    @Override
    public Collection<EnderDragonPart> getEnderDragonParts() {
        return List.of();
    }

    @Override
    public TickManager getTickManager() {
        return this.world.getTickManager();
    }

    @Nullable
    @Override
    public MapState getMapState(MapIdComponent id) {
        return null;
    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.world.getRecipeManager();
    }

    @Override
    protected EntityLookup<Entity> getEntityLookup() {
        return new EntityLookup<Entity>() {
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
            public Iterable<Entity> iterate() {
                return List.of();
            }

            @Override
            public <U extends Entity> void forEach(TypeFilter<Entity, U> filter, LazyIterationConsumer<U> consumer) {
            }

            @Override
            public void forEachIntersects(Box box, Consumer<Entity> action) {
            }

            @Override
            public <U extends Entity> void forEachIntersects(TypeFilter<Entity, U> filter, Box box, LazyIterationConsumer<U> consumer) {
            }
        };
    }

    @Override
    public BrewingRecipeRegistry getBrewingRecipeRegistry() {
        return this.world.getBrewingRecipeRegistry();
    }

    @Override
    public FuelRegistry getFuelRegistry() {
        return this.world.getFuelRegistry();
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }

    @Override
    public void syncWorldEvent(@Nullable Entity source, int eventId, BlockPos pos, int data) {

    }

    @Override
    public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter) {

    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return 0;
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return List.of();
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return this.world.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return this.world.getEnabledFeatures();
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return new QueryableTickScheduler<Block>() {
            @Override
            public boolean isTicking(BlockPos pos, Block type) {
                return false;
            }

            @Override
            public void scheduleTick(OrderedTick<Block> orderedTick) {

            }

            @Override
            public boolean isQueued(BlockPos pos, Block type) {
                return false;
            }

            @Override
            public int getTickCount() {
                return 0;
            }
        };
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return new QueryableTickScheduler<Fluid>() {
            @Override
            public boolean isTicking(BlockPos pos, Fluid type) {
                return false;
            }

            @Override
            public void scheduleTick(OrderedTick<Fluid> orderedTick) {

            }

            @Override
            public boolean isQueued(BlockPos pos, Fluid type) {
                return false;
            }

            @Override
            public int getTickCount() {
                return 0;
            }
        };
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    public interface Provider {
        MirrorWorld slingshot$getMirror();
    }

    private class MirrorChunk extends WorldChunk {
        private final Chunk chunk;

        public MirrorChunk(Chunk chunk) {
            super(MirrorWorld.this, chunk.getPos(), UpgradeData.NO_UPGRADE_DATA,
                    new ChunkTickScheduler<>(), new ChunkTickScheduler<>(), 0L,
                    Util.make(new ChunkSection[chunk.getSectionArray().length], arr -> {
                        for (var i = 0; i < chunk.getSectionArray().length; i++) {
                            arr[i] = new MirrorChunkSection(chunk, i);
                        }
                    }), (EntityLoader)null, (BlendingData)null);
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
        public void setHeightmap(Heightmap.Type type, long[] heightmap) {}

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
        public Heightmap getHeightmap(Heightmap.Type type) {
            return this.chunk.getHeightmap(type);
        }


    }

    private class MirrorChunkSection extends ChunkSection {
        private final Chunk chunk;
        private final int index;

        public MirrorChunkSection(Chunk chunk, int i) {
            super(MirrorWorld.this.getPalettesFactory());
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
        public boolean isEmpty() {
            return getRealSection().isEmpty();
        }

        @Override
        public void calculateCounts() {

        }

        private ChunkSection getRealSection() {
            return this.chunk.getSectionArray()[index];
        }
    }

    private class MirrorChunkManager extends ChunkManager {
        final MirrorChunk empty = new MirrorChunk(new EmptyChunk(MirrorWorld.this, ChunkPos.ORIGIN, MirrorWorld.this.getRegistryManager().getEntryOrThrow(BiomeKeys.PLAINS)));

        @Nullable
        @Override
        public Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
            var chunk = MirrorWorld.this.world.getChunkManager().getChunk(x, z, leastStatus, false);
            return chunk != null ? new MirrorChunk(chunk) : (create ? empty : null);
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
            return MirrorWorld.this.world.getChunkManager().getLoadedChunkCount();
        }

        @Override
        public LightingProvider getLightingProvider() {
            return MirrorWorld.this.world.getLightingProvider();
        }

        @Override
        public BlockView getWorld() {
            return MirrorWorld.this;
        }
    }
}
