package eu.pb4.slingshot.mixin;

import eu.pb4.slingshot.util.NetHandlerExt;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin implements NetHandlerExt {

    @Shadow public ServerPlayer player;
    @Unique
    private int hasSelection = -1;

    @Override
    public void slingshot$setSelectionTick(int val) {
        hasSelection = val;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void clearSelection(CallbackInfo ci) {
        if (hasSelection != -1 && hasSelection != this.player.tickCount) {
            hasSelection = -1;
            this.player.displayClientMessage(Component.empty(), true);
        }
    }
}
