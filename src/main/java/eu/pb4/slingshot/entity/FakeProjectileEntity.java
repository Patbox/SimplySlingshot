package eu.pb4.slingshot.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class FakeProjectileEntity extends ProjectileEntity {
    public Entity entity = null;

    public FakeProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static FakeProjectileEntity wrap(World world, Vec3d pos, Entity entity) {
        var fake = new FakeProjectileEntity(SlingshotEntities.FAKE_PROJECTILE, world);
        fake.setPos(pos.getX(), pos.getY(), pos.getZ());
        fake.entity = entity;
        return fake;
    }

    @Override
    public void tick() {
        if (entity != null) {
            entity.setPosition(this.getPos());
            entity.setVelocity(this.getVelocity());
            this.getWorld().spawnEntity(entity);
        }
        this.discard();
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        if (this.entity == null) return;
        this.entity.writeData(view.get("entity"));
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.entity = EntityType.loadEntityWithPassengers(view, this.getWorld(), SpawnReason.LOAD, x -> x);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}
}
