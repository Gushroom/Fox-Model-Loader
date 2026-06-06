package rip.ysm.compat.carryon.neoforge;

import com.elfmcys.yesstevemodel.client.animation.molang.CtrlBinding;
import com.elfmcys.yesstevemodel.client.animation.predicate.PlayerAnimationPredicate;
import com.elfmcys.yesstevemodel.client.entity.CustomPlayerEntity;
import com.elfmcys.yesstevemodel.geckolib3.core.controller.CompositeAnimationController;
import com.elfmcys.yesstevemodel.geckolib3.core.controller.IAnimationController;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import rip.ysm.compat.carryon.CarryOnDataHelper;

import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

public final class CarryOnCompatImpl {
    private CarryOnCompatImpl() {
    }

    public static boolean isLoaded() {
        return CarryOnDataHelperImpl.isAvailable();
    }

    public static Optional<BiFunction<String, CustomPlayerEntity, IAnimationController<CustomPlayerEntity>>> getControllerFactory() {
        if (!isLoaded()) {
            return Optional.empty();
        }
        return Optional.of((name, animatable) -> new CompositeAnimationController<>(animatable, name, 0.1f, new PlayerAnimationPredicate()));
    }

    public static boolean isPlayerCarrying(Player player) {
        return isLoaded() && CarryOnDataHelperImpl.getCarryType(player) != CarryOnDataHelper.CarryType.NONE;
    }

    public static void registerBindings(CtrlBinding binding) {
        if (isLoaded()) {
            binding.livingEntityVar("carryon_type", ctx -> {
                Entity entity = ctx.entity();
                if (!(entity instanceof Player player)) {
                    return StringPool.EMPTY;
                }
                CarryOnDataHelper.CarryType type = CarryOnDataHelperImpl.getCarryType(player);
                return type == CarryOnDataHelper.CarryType.NONE ? StringPool.EMPTY : type.name().toLowerCase(Locale.ENGLISH);
            });
            binding.livingEntityVar("carryon_is_princess", ctx -> CarryOnDataHelperImpl.isPlayerCarrying(ctx.entity()));
        } else {
            binding.livingEntityVar("carryon_type", ctx -> StringPool.EMPTY);
            binding.livingEntityVar("carryon_is_princess", ctx -> false);
        }
    }
}
