package rip.ysm.compat.carryon;

import com.elfmcys.yesstevemodel.YesSteveModel;
import dev.architectury.platform.Platform;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

/**
 * Reads Carry On carry state via reflection so neither platform needs a compile-time dependency.
 * Verified against Carry On's 1.21.1 branch: {@code CarryOnDataManager.getCarryData(Player)} and
 * {@code CarryOnData.isCarrying(CarryType)} are unchanged from 1.20.1.
 */
public final class CarryOnDataHelper {

    private static final String MOD_ID = "carryon";

    public enum CarryType {
        ENTITY,
        BLOCK,
        PLAYER,
        NONE
    }

    private CarryOnDataHelper() {
    }

    public static boolean isAvailable() {
        return Platform.isModLoaded(MOD_ID) && BridgeHolder.INSTANCE != null;
    }

    /**
     * True when this entity is being princess-carried: its vehicle is a player whose Carry On
     * state says it is carrying a PLAYER. This matches the (confusingly named) semantics of the
     * original OpenYSM {@code CarryOnDataHelper.isPlayerCarrying} and is what gates the
     * {@code carryon:princess} animation on the carried player.
     */
    public static boolean isPlayerCarrying(LivingEntity livingEntity) {
        Entity vehicle = livingEntity.getVehicle();
        return vehicle instanceof Player carrier && getCarryType(carrier) == CarryType.PLAYER;
    }

    /**
     * What this player is currently carrying, NONE if Carry On is absent or nothing is carried.
     */
    public static CarryType getCarryType(Player player) {
        return isAvailable() ? BridgeHolder.INSTANCE.getCarryType(player) : CarryType.NONE;
    }

    private static final class BridgeHolder {
        private static final Bridge INSTANCE = Bridge.create();

        private BridgeHolder() {
        }
    }

    private record Bridge(Method getCarryData, Method isCarrying, Map<CarryType, Object> nativeTypes) {

        static Bridge create() {
            if (!Platform.isModLoaded(MOD_ID)) {
                return null;
            }
            try {
                Class<?> dataManagerClass = Class.forName("tschipp.carryon.common.carry.CarryOnDataManager");
                Class<?> carryDataClass = Class.forName("tschipp.carryon.common.carry.CarryOnData");
                Class<?> carryTypeClass = Class.forName("tschipp.carryon.common.carry.CarryOnData$CarryType");

                Map<CarryType, Object> nativeTypes = new EnumMap<>(CarryType.class);
                nativeTypes.put(CarryType.BLOCK, enumValue(carryTypeClass, "BLOCK"));
                nativeTypes.put(CarryType.ENTITY, enumValue(carryTypeClass, "ENTITY"));
                nativeTypes.put(CarryType.PLAYER, enumValue(carryTypeClass, "PLAYER"));
                return new Bridge(
                        dataManagerClass.getMethod("getCarryData", Player.class),
                        carryDataClass.getMethod("isCarrying", carryTypeClass),
                        nativeTypes);
            } catch (ReflectiveOperationException | LinkageError e) {
                YesSteveModel.LOGGER.warn("Carry On is present but its compatibility bridge failed to initialize", e);
                return null;
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Object enumValue(Class<?> enumClass, String name) {
            return Enum.valueOf((Class<? extends Enum>) enumClass.asSubclass(Enum.class), name);
        }

        CarryType getCarryType(Player player) {
            try {
                Object carryData = getCarryData.invoke(null, player);
                for (Map.Entry<CarryType, Object> entry : nativeTypes.entrySet()) {
                    if (Boolean.TRUE.equals(isCarrying.invoke(carryData, entry.getValue()))) {
                        return entry.getKey();
                    }
                }
            } catch (ReflectiveOperationException e) {
                YesSteveModel.LOGGER.warn("Failed to read Carry On carry state", e);
            }
            return CarryType.NONE;
        }
    }
}
