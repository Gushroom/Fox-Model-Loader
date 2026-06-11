package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({IntegratedServer.class})
public abstract class IntegratedServerMixin {
    @Inject(method = {"initServer()Z"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;setUsesAuthentication(Z)V", shift = At.Shift.AFTER))
    private void disableDevLanAuthentication(CallbackInfoReturnable<Boolean> ci) {
        if (Boolean.getBoolean("yes_steve_model.debug.disable_lan_auth")) {
            ((IntegratedServer) (Object) this).setUsesAuthentication(false);
            YesSteveModel.LOGGER.warn("Disabled integrated-server LAN authentication for YSM debug run");
        }
    }
}
