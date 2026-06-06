package rip.ysm.compat.touhoulittlemaid.neoforge;

import net.minecraft.world.entity.Entity;

import java.util.Optional;

public final class MaidCapabilityBridgeImpl {
    private MaidCapabilityBridgeImpl() {
    }

    public static Optional<Object> get(Entity entity) {
        return Optional.empty();
    }
}
