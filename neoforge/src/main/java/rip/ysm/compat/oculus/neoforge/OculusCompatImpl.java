package rip.ysm.compat.oculus.neoforge;

public final class OculusCompatImpl {
    private OculusCompatImpl() {
    }

    public static boolean isLoaded() {
        return false;
    }

    public static boolean isPBRActive() {
        return false;
    }

    public static void updatePBRState() {
    }

    public static boolean isShaderPackInUse() {
        return false;
    }

    public static boolean isRenderingShadowPass() {
        return false;
    }
}
