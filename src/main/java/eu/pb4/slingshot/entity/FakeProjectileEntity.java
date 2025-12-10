package eu.pb4.slingshot.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public final class FakeProjectileEntity extends Projectile {
    public Entity entity = null;

    public FakeProjectileEntity(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    public static FakeProjectileEntity wrap(Level world, Vec3 pos, Entity entity) {
        var fake = new FakeProjectileEntity(SlingshotEntities.FAKE_PROJECTILE, world);
        fake.setPosRaw(pos.x(), pos.y(), pos.z());
        fake.entity = entity;
        return fake;
    }

    @Override
    public void tick() {
        if (entity != null) {
            entity.setPos(this.position());
            entity.setDeltaMovement(this.getDeltaMovement());
            this.level().addFreshEntity(entity);
        }
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        if (this.entity == null) return;
        this.entity.saveWithoutId(view.child("entity"));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.entity = EntityType.loadEntityRecursive(view, this.level(), EntitySpawnReason.LOAD, x -> x);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}
}
