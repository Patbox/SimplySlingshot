package eu.pb4.slingshot.mixin;

import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.util.BounceableExt;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity implements BounceableExt {
    @Unique
    private int bounces = 0;

    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @Override
    public void slingshot$setBounces(int bounces) {
        this.bounces = bounces;
    }

    @Override
    public int slingshot$getBounces() {
        return this.bounces;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void spawnBounceParticles(CallbackInfo ci) {
        if (this.bounces > 0 && this.age % 4 == 0 && this.getEntityWorld() instanceof ServerWorld world) {
            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.getDefaultState()), this.getX(), this.getY(), this.getZ(), 0,
                    this.getRandom().nextFloat() - 0.5, this.getRandom().nextFloat() - 0.5, this.getRandom().nextFloat() - 0.5, 0.1f);
        }
    }

    @Inject(method = "hitOrDeflect", at = @At("HEAD"), cancellable = true)
    private void handleBounce(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> cir) {
        if (hitResult instanceof BlockHitResult result && this.bounces-- > 0) {
            var old = this.getVelocity();
            this.setVelocity(this.getVelocity().multiply(
                    result.getSide().getAxis().choose(-1, 1, 1),
                    result.getSide().getAxis().choose(1, -1, 1),
                    result.getSide().getAxis().choose(1, 1, -1)
            ));
            this.velocityModified = true;
            this.velocityDirty = true;
            this.playSound(SoundEvents.BLOCK_SLIME_BLOCK_FALL, 1, 1);
            this.onBouncedOff(result);
            SlingshotEvents.ON_BOUNCE.invoker().onBounce((ProjectileEntity) (Object) this, old, hitResult);
            cir.setReturnValue(ProjectileDeflection.SIMPLE);
        }
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeBounces(WriteView view, CallbackInfo ci) {
        if (this.bounces > 0) {
            view.putInt("slingshot:bounces", this.bounces);
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readBounces(ReadView view, CallbackInfo ci) {
        this.bounces = view.getInt("slingshot:bounces", 0);
    }
}
