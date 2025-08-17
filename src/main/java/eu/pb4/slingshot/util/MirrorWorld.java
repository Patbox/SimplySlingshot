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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MirrorWorld extends World {
    private final World world;
    private final FakeChunkManager chunkManager = new FakeChunkManager(this);

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
    public void createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, RegistryEntry<SoundEvent> soundEvent) {
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

    public interface Provider {
        MirrorWorld slingshot$getMirror();
    }
}
