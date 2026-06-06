package com.elfmcys.yesstevemodel.capability.neoforge;

import com.elfmcys.yesstevemodel.capability.ModelInfoCapability;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class ModelInfoCapabilityImpl {
    private ModelInfoCapabilityImpl() {
    }

    public static Optional<ModelInfoCapability> get(Player player) {
        return Optional.of(player.getData(NeoForgeCapabilityTypes.MODEL_INFO));
    }
}
