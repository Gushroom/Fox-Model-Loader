package com.elfmcys.yesstevemodel.capability.neoforge;

import com.elfmcys.yesstevemodel.capability.StarModelsCapability;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class StarModelsCapabilityImpl {
    private StarModelsCapabilityImpl() {
    }

    public static Optional<StarModelsCapability> get(Player player) {
        return Optional.of(player.getData(NeoForgeCapabilityTypes.STAR_MODELS));
    }
}
