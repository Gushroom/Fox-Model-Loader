package rip.ysm.api.config.neoforge;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import rip.ysm.api.config.ConfigRegistration;
import rip.ysm.api.config.YsmConfigSpec;

public final class ConfigRegistrationImpl {
    private static ModContainer container;

    private ConfigRegistrationImpl() {
    }

    public static void setContainer(ModContainer modContainer) {
        container = modContainer;
    }

    public static void register(String modId, ConfigRegistration.Type type, YsmConfigSpec spec) {
        if (container == null) {
            throw new IllegalStateException("NeoForge config container has not been initialized for " + modId);
        }
        container.registerConfig(mapType(type), buildSpec(spec));
    }

    private static ModConfig.Type mapType(ConfigRegistration.Type type) {
        return switch (type) {
            case CLIENT -> ModConfig.Type.CLIENT;
            case SERVER -> ModConfig.Type.SERVER;
        };
    }

    private static ModConfigSpec buildSpec(YsmConfigSpec spec) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        for (YsmConfigSpec.ConfigValue<?> value : spec.values()) {
            applyComments(builder, value);
            bindValue(builder, value);
        }
        return builder.build();
    }

    private static void applyComments(ModConfigSpec.Builder builder, YsmConfigSpec.ConfigValue<?> value) {
        if (!value.comments().isEmpty()) {
            builder.comment(value.comments().toArray(String[]::new));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void bindValue(ModConfigSpec.Builder builder, YsmConfigSpec.ConfigValue<?> value) {
        if (value instanceof YsmConfigSpec.BooleanValue booleanValue) {
            ModConfigSpec.BooleanValue nativeValue = builder.define(value.key(), booleanValue.defaultValue().booleanValue());
            booleanValue.bind(new BoundValue<>(nativeValue));
        } else if (value instanceof YsmConfigSpec.IntValue intValue) {
            ModConfigSpec.IntValue nativeValue = builder.defineInRange(value.path(), intValue.defaultValue(), intValue.min(), intValue.max());
            intValue.bind(new BoundValue<>(nativeValue));
        } else if (value instanceof YsmConfigSpec.DoubleValue doubleValue) {
            ModConfigSpec.DoubleValue nativeValue = builder.defineInRange(value.path(), doubleValue.defaultValue(), doubleValue.min(), doubleValue.max());
            doubleValue.bind(new BoundValue<>(nativeValue));
        } else if (value instanceof YsmConfigSpec.EnumValue enumValue) {
            ModConfigSpec.EnumValue<?> nativeValue = builder.defineEnum(value.path(), (Enum) enumValue.defaultValue());
            enumValue.bind(new BoundValue(nativeValue));
        } else {
            ModConfigSpec.ConfigValue<?> nativeValue = builder.define(value.path(), value.defaultValue());
            ((YsmConfigSpec.ConfigValue) value).bind(new BoundValue(nativeValue));
        }
    }

    private record BoundValue<T>(ModConfigSpec.ConfigValue<T> delegate) implements YsmConfigSpec.NativeValue<T> {
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
