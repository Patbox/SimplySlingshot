package eu.pb4.slingshot.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NoteBlock.class)
public interface NoteBlockAccessor {
    @Invoker
    void callPlayNote(@Nullable Entity entity, BlockState state, Level world, BlockPos pos);
}
