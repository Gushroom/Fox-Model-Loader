package com.elfmcys.yesstevemodel.capability.neoforge;

import com.elfmcys.yesstevemodel.capability.ProjectileCapability;
import com.elfmcys.yesstevemodel.capability.neoforge.client.ProjectileCapabilityClientStore;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Optional;

public final class ProjectileCapabilityImpl {
    private ProjectileCapabilityImpl() {
    }

    public static Optional<ProjectileCapability> get(Entity entity) {
        return entity instanceof Projectile projectile ? get(projectile) : Optional.empty();
    }

    public static Optional<ProjectileCapability> get(Projectile projectile) {
        if (FMLEnvironment.dist != Dist.CLIENT || !projectile.level().isClientSide()) {
            return Optional.empty();
        }
        return ProjectileCapabilityClientStore.get(projectile);
    }
}
