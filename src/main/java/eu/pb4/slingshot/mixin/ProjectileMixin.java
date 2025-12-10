package eu.pb4.slingshot.mixin;

import eu.pb4.slingshot.SlingshotEvents;
import eu.pb4.slingshot.util.BounceableExt;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity implements BounceableExt {
    @Unique
    private int bounces = 0;

    public ProjectileMixin(EntityType<?> type, Level world) {
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
        if (this.bounces > 0 && this.tickCount % 4 == 0 && this.level() instanceof ServerLevel world) {
            world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.defaultBlockState()), this.getX(), this.getY(), this.getZ(), 0,
                    this.getRandom().nextFloat() - 0.5, this.getRandom().nextFloat() - 0.5, this.getRandom().nextFloat() - 0.5, 0.1f);
        }
    }

    @Inject(method = "hitTargetOrDeflectSelf", at = @At("HEAD"), cancellable = true)
    private void handleBounce(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> cir) {
        if (hitResult instanceof BlockHitResult result && this.bounces-- > 0) {
            var old = this.getDeltaMovement();
            this.setDeltaMovement(this.getDeltaMovement().multiply(
                    result.getDirection().getAxis().choose(-1, 1, 1),
                    result.getDirection().getAxis().choose(1, -1, 1),
                    result.getDirection().getAxis().choose(1, 1, -1)
            ));
            this.hurtMarked = true;
            this.needsSync = true;
            this.playSound(SoundEvents.SLIME_BLOCK_FALL, 1, 1);
            this.onBouncedOff(result);
            SlingshotEvents.ON_BOUNCE.invoker().onBounce((Projectile) (Object) this, old, hitResult);
            cir.setReturnValue(ProjectileDeflection.REVERSE);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeBounces(ValueOutput view, CallbackInfo ci) {
        if (this.bounces > 0) {
            view.putInt("slingshot:bounces", this.bounces);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readBounces(ValueInput view, CallbackInfo ci) {
        this.bounces = view.getIntOr("slingshot:bounces", 0);
    }
}
