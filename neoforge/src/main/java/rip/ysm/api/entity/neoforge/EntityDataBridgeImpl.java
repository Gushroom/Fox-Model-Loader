package rip.ysm.api.entity.neoforge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class EntityDataBridgeImpl {
    private EntityDataBridgeImpl() {
    }

    public static CompoundTag getPersistentData(Entity entity) {
        return entity.getPersistentData();
    }

    public static boolean shouldRiderSit(Entity vehicle) {
        return true;
    }
}
