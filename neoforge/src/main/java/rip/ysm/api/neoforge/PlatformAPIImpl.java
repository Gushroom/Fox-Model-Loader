package rip.ysm.api.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public final class PlatformAPIImpl {
    private PlatformAPIImpl() {
    }

    public static boolean isServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    public static String getPlatformName() {
        return "neoforge";
    }
}
