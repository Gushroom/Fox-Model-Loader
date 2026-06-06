package com.elfmcys.yesstevemodel.capability.neoforge;

import com.elfmcys.yesstevemodel.capability.VehicleCapability;
import com.elfmcys.yesstevemodel.capability.neoforge.client.VehicleCapabilityClientStore;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Optional;

public final class VehicleCapabilityImpl {
    private VehicleCapabilityImpl() {
    }

    public static Optional<VehicleCapability> get(Entity entity) {
        if (FMLEnvironment.dist != Dist.CLIENT || !entity.level().isClientSide()) {
            return Optional.empty();
        }
        return VehicleCapabilityClientStore.get(entity);
    }
}
