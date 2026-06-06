package rip.ysm.compat.swem.neoforge;

import com.elfmcys.yesstevemodel.client.animation.molang.CtrlBinding;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import net.minecraft.world.entity.LivingEntity;

public final class SWEMCompatImpl {
    private SWEMCompatImpl() {
    }

    public static boolean isLoaded() {
        return false;
    }

    public static String getHorseGaitName(LivingEntity livingEntity) {
        return StringPool.EMPTY;
    }

    public static void registerControllerFunctions(CtrlBinding ctrlBinding) {
    }
}
