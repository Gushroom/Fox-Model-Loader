package com.elfmcys.yesstevemodel.neoforge;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.neoforge.NeoForgeCapabilityTypes;
import com.elfmcys.yesstevemodel.event.CapabilityEvent;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import rip.ysm.api.config.neoforge.ConfigRegistrationImpl;
import rip.ysm.api.network.neoforge.YSMChannelImpl;

@Mod(YesSteveModel.MOD_ID)
public final class YesSteveModelNeoForge {
    public YesSteveModelNeoForge(IEventBus modBus, ModContainer container) {
        ConfigRegistrationImpl.setContainer(container);
        NeoForgeCapabilityTypes.register(modBus);
        modBus.addListener(YSMChannelImpl::registerPayloadHandlers);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.StartTracking event) -> {
            if (event.getEntity() instanceof ServerPlayer observer) {
                CapabilityEvent.onStartTracking(event.getTarget(), observer);
            }
        });
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForgeClientEvents.register(modBus, container);
        }
        YesSteveModel.init();
        NetworkHandler.init();
    }
}
