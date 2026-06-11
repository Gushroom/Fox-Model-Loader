package com.elfmcys.yesstevemodel.fabric;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.event.CapabilityEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;

public final class YesSteveModelFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YesSteveModel.init();
        EntityTrackingEvents.START_TRACKING.register(CapabilityEvent::onStartTracking);
    }
}
