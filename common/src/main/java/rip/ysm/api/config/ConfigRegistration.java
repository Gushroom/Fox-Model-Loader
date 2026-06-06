package rip.ysm.api.config;

import dev.architectury.injectables.annotations.ExpectPlatform;

public final class ConfigRegistration {
    public enum Type {
        CLIENT,
        SERVER
    }

    private ConfigRegistration() {
    }

    @ExpectPlatform
    public static void register(String modId, Type type, YsmConfigSpec spec) {
        throw new AssertionError();
    }
}
