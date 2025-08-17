package eu.pb4.slingshot.mixin;

import eu.pb4.slingshot.util.NetHandlerExt;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements NetHandlerExt {

    @Shadow public ServerPlayerEntity player;
    @Unique
    private int hasSelection = -1;

    @Override
    public void slingshot$setSelectionTick(int val) {
        hasSelection = val;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void clearSelection(CallbackInfo ci) {
        if (hasSelection != -1 && hasSelection != this.player.age) {
            hasSelection = -1;
            this.player.sendMessage(Text.empty(), true);
        }
    }
}
