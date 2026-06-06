package rip.ysm.api.attribute.neoforge;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;

public final class ForgeAttributesImpl {
    private ForgeAttributesImpl() {
    }

    @Nullable
    public static Attribute blockReach() {
        return Attributes.BLOCK_INTERACTION_RANGE.value();
    }

    @Nullable
    public static Attribute entityReach() {
        return Attributes.ENTITY_INTERACTION_RANGE.value();
    }

    @Nullable
    public static Attribute swimSpeed() {
        return NeoForgeMod.SWIM_SPEED.value();
    }

    @Nullable
    public static Attribute entityGravity() {
        return Attributes.GRAVITY.value();
    }

    @Nullable
    public static Attribute stepHeightAddition() {
        return Attributes.STEP_HEIGHT.value();
    }

    @Nullable
    public static Attribute nametagDistance() {
        return NeoForgeMod.NAMETAG_DISTANCE.value();
    }
}
