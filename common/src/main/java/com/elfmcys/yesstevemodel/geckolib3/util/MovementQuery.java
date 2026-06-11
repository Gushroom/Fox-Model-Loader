package com.elfmcys.yesstevemodel.geckolib3.util;

import com.elfmcys.yesstevemodel.geckolib3.core.EntityFrameStateTracker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class MovementQuery {
    public static final float EPSILON = 1.0E-4f;

    private MovementQuery() {
    }

    /**
     * Faithful to OpenYSM 1.20.1 {@code query.ground_speed}: derived purely from the entity's
     * own delta movement. Never synthesize speed from render-frame position deltas — network
     * interpolation jitter of remote players on dedicated servers makes that read as movement
     * while standing still.
     */
    public static float getGroundSpeed(Entity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        float speed = 20.0f * Mth.sqrt((float) ((deltaMovement.x * deltaMovement.x) + (deltaMovement.z * deltaMovement.z)));
        return Float.isFinite(speed) ? speed : 0.0f;
    }

    /**
     * Faithful to OpenYSM 1.20.1 {@code query.vertical_speed}: frame-tracker position delta over
     * frame time delta. No delta-movement fallback — grounded entities permanently carry a small
     * downward delta movement from gravity, which would report idle entities as descending.
     */
    public static float getVerticalSpeed(Entity entity, EntityFrameStateTracker<?> tracker) {
        float timeDelta = tracker.getTimeDelta();
        if (!Float.isFinite(timeDelta) || timeDelta <= EPSILON) {
            return 0.0f;
        }
        float speed = (20.0f * (float) tracker.getPositionDelta().y) / timeDelta;
        return Float.isFinite(speed) ? speed : 0.0f;
    }
}
