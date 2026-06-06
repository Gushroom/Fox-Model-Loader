package com.elfmcys.yesstevemodel.capability.neoforge;

import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.elfmcys.yesstevemodel.capability.neoforge.client.PlayerCapabilityClientStore;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Optional;

public final class PlayerCapabilityImpl {
    private PlayerCapabilityImpl() {
    }

    public static Optional<PlayerCapability> get(Player player) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return Optional.empty();
        }
        return PlayerCapabilityClientStore.get(player);
    }

    public static Optional<PlayerCapability> get(Entity entity) {
        return entity instanceof Player player ? get(player) : Optional.empty();
    }
}
