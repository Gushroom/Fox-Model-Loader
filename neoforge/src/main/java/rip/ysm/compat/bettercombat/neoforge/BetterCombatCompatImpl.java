package rip.ysm.compat.bettercombat.neoforge;

import com.elfmcys.yesstevemodel.client.animation.molang.CtrlBinding;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.LoadingModList;

import java.lang.reflect.Method;

public final class BetterCombatCompatImpl {
    private static final String MOD_ID = "bettercombat";
    private static final String PLAYER_INTERFACE = "net.bettercombat.api.EntityPlayer_BetterCombat";
    private static final String ATTACK_ANIMATION = "bcombat_attack_animation";

    private static Boolean isLoaded;
    private static Method getCurrentAttack;

    private BetterCombatCompatImpl() {
    }

    public static boolean isLoaded() {
        if (isLoaded == null) {
            isLoaded = LoadingModList.get().getModFileById(MOD_ID) != null;
        }
        return isLoaded;
    }

    public static void registerBindings(CtrlBinding binding) {
        binding.clientPlayerEntityVar(ATTACK_ANIMATION, ctx -> {
            if (!isLoaded()) {
                return StringPool.EMPTY;
            }
            return getAttackAnimation(ctx.entity());
        });
    }

    private static String getAttackAnimation(Object player) {
        Object currentAttack = invokeCurrentAttack(player);
        if (currentAttack == null) {
            return StringPool.EMPTY;
        }

        Object attack = invoke(currentAttack, "attack");
        Object animation = attack == null ? null : invoke(attack, "animation");
        if (!(animation instanceof String animationName) || animationName.isEmpty()) {
            return StringPool.EMPTY;
        }

        return normalizeAnimationName(animationName);
    }

    private static Object invokeCurrentAttack(Object player) {
        try {
            if (getCurrentAttack == null) {
                Class<?> playerInterface = Class.forName(PLAYER_INTERFACE);
                if (!playerInterface.isInstance(player)) {
                    return null;
                }
                getCurrentAttack = playerInterface.getMethod("getCurrentAttack");
            }
            return getCurrentAttack.invoke(player);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) {
            return null;
        }
    }

    private static Object invoke(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static String normalizeAnimationName(String animationName) {
        ResourceLocation location = ResourceLocation.tryParse(animationName);
        if (location != null) {
            return location.getPath();
        }

        int namespaceIndex = animationName.indexOf(':');
        if (namespaceIndex >= 0 && namespaceIndex + 1 < animationName.length()) {
            return animationName.substring(namespaceIndex + 1);
        }
        return animationName;
    }
}
