package rip.ysm.compat.carryon.neoforge;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import rip.ysm.compat.carryon.CarryOnDataHelper;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

public final class CarryOnDataHelperImpl {
    private static final String MOD_ID = "carryon";

    private CarryOnDataHelperImpl() {
    }

    public static boolean isPlayerCarrying(LivingEntity livingEntity) {
        Entity vehicle = livingEntity.getVehicle();
        return vehicle instanceof Player player && getCarryType(player) == CarryOnDataHelper.CarryType.PLAYER;
    }

    public static CarryOnDataHelper.CarryType getCarryType(Player player) {
        if (!ModList.get().isLoaded(MOD_ID) || !BridgeHolder.INSTANCE.isAvailable()) {
            return CarryOnDataHelper.CarryType.NONE;
        }
        return BridgeHolder.INSTANCE.getCarryType(player);
    }

    static boolean isAvailable() {
        return ModList.get().isLoaded(MOD_ID) && BridgeHolder.INSTANCE.isAvailable();
    }

    private static final class BridgeHolder {
        private static final Bridge INSTANCE = Bridge.create();

        private BridgeHolder() {
        }
    }

    private record Bridge(Method getCarryData, Method isCarrying, Map<CarryOnDataHelper.CarryType, Object> nativeTypes) {
        static Bridge create() {
            try {
                Class<?> dataManagerClass = Class.forName("tschipp.carryon.common.carry.CarryOnDataManager");
                Class<?> carryDataClass = Class.forName("tschipp.carryon.common.carry.CarryOnData");
                Class<?> carryTypeClass = Class.forName("tschipp.carryon.common.carry.CarryOnData$CarryType");

                Method getCarryData = dataManagerClass.getMethod("getCarryData", Player.class);
                Method isCarrying = carryDataClass.getMethod("isCarrying", carryTypeClass);

                Map<CarryOnDataHelper.CarryType, Object> nativeTypes = new EnumMap<>(CarryOnDataHelper.CarryType.class);
                nativeTypes.put(CarryOnDataHelper.CarryType.BLOCK, enumValue(carryTypeClass, "BLOCK"));
                nativeTypes.put(CarryOnDataHelper.CarryType.ENTITY, enumValue(carryTypeClass, "ENTITY"));
                nativeTypes.put(CarryOnDataHelper.CarryType.PLAYER, enumValue(carryTypeClass, "PLAYER"));
                return new Bridge(getCarryData, isCarrying, nativeTypes);
            } catch (ReflectiveOperationException | LinkageError e) {
                YesSteveModel.LOGGER.warn("Carry On compatibility bridge is unavailable", e);
                return new Bridge(null, null, Map.of());
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Object enumValue(Class<?> enumClass, String name) {
            return Enum.valueOf((Class<? extends Enum>) enumClass.asSubclass(Enum.class), name);
        }

        boolean isAvailable() {
            return getCarryData != null && isCarrying != null && !nativeTypes.isEmpty();
        }

        CarryOnDataHelper.CarryType getCarryType(Player player) {
            try {
                Object carryData = getCarryData.invoke(null, player);
                if (isCarrying(carryData, CarryOnDataHelper.CarryType.BLOCK)) {
                    return CarryOnDataHelper.CarryType.BLOCK;
                }
                if (isCarrying(carryData, CarryOnDataHelper.CarryType.ENTITY)) {
                    return CarryOnDataHelper.CarryType.ENTITY;
                }
                if (isCarrying(carryData, CarryOnDataHelper.CarryType.PLAYER)) {
                    return CarryOnDataHelper.CarryType.PLAYER;
                }
            } catch (ReflectiveOperationException | LinkageError e) {
                YesSteveModel.LOGGER.warn("Failed to read Carry On carry state", e);
            }
            return CarryOnDataHelper.CarryType.NONE;
        }

        private boolean isCarrying(Object carryData, CarryOnDataHelper.CarryType type) throws ReflectiveOperationException {
            Object nativeType = nativeTypes.get(type);
            return nativeType != null && Boolean.TRUE.equals(isCarrying.invoke(carryData, nativeType));
        }
    }
}
