package com.elfmcys.yesstevemodel.capability.neoforge;

import com.elfmcys.yesstevemodel.capability.AuthModelsCapability;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class AuthModelsCapabilityImpl {
    private AuthModelsCapabilityImpl() {
    }

    public static Optional<AuthModelsCapability> get(Player player) {
        return Optional.of(player.getData(NeoForgeCapabilityTypes.AUTH_MODELS));
    }
}
