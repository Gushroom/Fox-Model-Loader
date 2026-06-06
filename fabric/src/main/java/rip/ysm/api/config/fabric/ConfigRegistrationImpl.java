package rip.ysm.api.config.fabric;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import rip.ysm.api.config.ConfigRegistration;
import rip.ysm.api.config.YsmConfigSpec;

public final class ConfigRegistrationImpl {

    private ConfigRegistrationImpl() {
    }

    public static void register(String modId, ConfigRegistration.Type type, YsmConfigSpec spec) {
        ForgeConfigRegistry.INSTANCE.register(modId, mapType(type), buildSpec(spec));
    }

    private static ModConfig.Type mapType(ConfigRegistration.Type type) {
        return switch (type) {
            case CLIENT -> ModConfig.Type.CLIENT;
            case SERVER -> ModConfig.Type.SERVER;
        };
    }

    private static ForgeConfigSpec buildSpec(YsmConfigSpec spec) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        for (YsmConfigSpec.ConfigValue<?> value : spec.values()) {
            applyComments(builder, value);
            bindValue(builder, value);
        }
        return builder.build();
    }

    private static void applyComments(ForgeConfigSpec.Builder builder, YsmConfigSpec.ConfigValue<?> value) {
        if (!value.comments().isEmpty()) {
            builder.comment(value.comments().toArray(String[]::new));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void bindValue(ForgeConfigSpec.Builder builder, YsmConfigSpec.ConfigValue<?> value) {
        if (value instanceof YsmConfigSpec.BooleanValue booleanValue) {
            ForgeConfigSpec.BooleanValue nativeValue = builder.define(value.key(), booleanValue.defaultValue().booleanValue());
            booleanValue.bind(new BoundValue<>(nativeValue));
        } else if (value instanceof YsmConfigSpec.IntValue intValue) {
            ForgeConfigSpec.IntValue nativeValue = builder.defineInRange(value.path(), intValue.defaultValue(), intValue.min(), intValue.max());
            intValue.bind(new BoundValue<>(nativeValue));
        } else if (value instanceof YsmConfigSpec.DoubleValue doubleValue) {
            ForgeConfigSpec.DoubleValue nativeValue = builder.defineInRange(value.path(), doubleValue.defaultValue(), doubleValue.min(), doubleValue.max());
            doubleValue.bind(new BoundValue<>(nativeValue));
        } else if (value instanceof YsmConfigSpec.EnumValue enumValue) {
            ForgeConfigSpec.EnumValue<?> nativeValue = builder.defineEnum(value.path(), (Enum) enumValue.defaultValue());
            enumValue.bind(new BoundValue(nativeValue));
        } else {
            ForgeConfigSpec.ConfigValue<?> nativeValue = builder.define(value.path(), value.defaultValue());
            ((YsmConfigSpec.ConfigValue) value).bind(new BoundValue(nativeValue));
        }
    }

    private record BoundValue<T>(ForgeConfigSpec.ConfigValue<T> delegate) implements YsmConfigSpec.NativeValue<T> {
        @Override
        public T get() {
            return delegate.get();
        }

        @Override
        public void set(T value) {
            delegate.set(value);
        }

        @Override
        public void save() {
            delegate.save();
        }
    }
}
