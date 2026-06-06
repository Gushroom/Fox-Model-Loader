package com.elfmcys.yesstevemodel.capability.neoforge;

import com.elfmcys.yesstevemodel.capability.ProjectileModelCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.Optional;

public final class ProjectileModelCapabilityImpl {
    private ProjectileModelCapabilityImpl() {
    }

    public static Optional<ProjectileModelCapability> get(Entity entity) {
        if (!(entity instanceof Projectile projectile)) {
            return Optional.empty();
        }
        return get(projectile);
    }

    public static Optional<ProjectileModelCapability> get(Projectile projectile) {
        return Optional.of(projectile.getData(NeoForgeCapabilityTypes.PROJECTILE_MODEL));
    }
}
