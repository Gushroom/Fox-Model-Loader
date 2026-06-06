package rip.ysm.api.config;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class YsmConfigSpec {
    private final List<ConfigValue<?>> values;

    private YsmConfigSpec(List<ConfigValue<?>> values) {
        this.values = List.copyOf(values);
    }

    public List<ConfigValue<?>> values() {
        return values;
    }

    public static final class Builder {
        private final List<ConfigValue<?>> values = new ArrayList<>();
        private final ArrayDeque<String> path = new ArrayDeque<>();
        private final List<String> pendingComments = new ArrayList<>();

        public Builder push(String name) {
            path.addLast(name);
            return this;
        }

        public Builder pop() {
            if (!path.isEmpty()) {
                path.removeLast();
            }
            return this;
        }

        public Builder comment(String comment) {
            pendingComments.add(comment);
            return this;
        }

        public BooleanValue define(String key, boolean defaultValue) {
            BooleanValue value = new BooleanValue(fullPath(key), takeComments(), defaultValue);
            values.add(value);
            return value;
        }

        public IntValue defineInRange(String key, int defaultValue, int min, int max) {
            IntValue value = new IntValue(fullPath(key), takeComments(), defaultValue, min, max);
            values.add(value);
            return value;
        }

        public DoubleValue defineInRange(String key, double defaultValue, double min, double max) {
            DoubleValue value = new DoubleValue(fullPath(key), takeComments(), defaultValue, min, max);
            values.add(value);
            return value;
        }

        public <T> ConfigValue<T> define(String key, T defaultValue) {
            ConfigValue<T> value = new ConfigValue<>(fullPath(key), takeComments(), defaultValue);
            values.add(value);
            return value;
        }

        public <E extends Enum<E>> EnumValue<E> defineEnum(String key, E defaultValue) {
            EnumValue<E> value = new EnumValue<>(fullPath(key), takeComments(), defaultValue);
            values.add(value);
            return value;
        }

        public YsmConfigSpec build() {
            return new YsmConfigSpec(values);
        }

        private List<String> fullPath(String key) {
            List<String> result = new ArrayList<>(path);
            result.add(key);
            return result;
        }

        private List<String> takeComments() {
            if (pendingComments.isEmpty()) {
                return List.of();
            }
            List<String> comments = List.copyOf(pendingComments);
            pendingComments.clear();
            return comments;
        }
    }

    public interface NativeValue<T> {
        T get();

        void set(T value);

        void save();
    }

    public static class ConfigValue<T> implements Supplier<T> {
        private final List<String> path;
        private final List<String> comments;
        private final T defaultValue;
        private T value;
        private NativeValue<T> nativeValue;

        protected ConfigValue(List<String> path, List<String> comments, T defaultValue) {
            this.path = List.copyOf(path);
            this.comments = List.copyOf(comments);
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public String key() {
            if (path.isEmpty()) {
                return "";
            }
            return String.join(".", path);
        }

        public List<String> path() {
            return path;
        }

        public List<String> comments() {
            return comments;
        }

        public T defaultValue() {
            return defaultValue;
        }

        @Override
        public T get() {
            return nativeValue == null ? value : nativeValue.get();
        }

        public void set(T value) {
            if (nativeValue == null) {
                this.value = value;
            } else {
                nativeValue.set(value);
            }
        }

        public void save() {
            if (nativeValue != null) {
                nativeValue.save();
            }
        }

        public void bind(NativeValue<T> nativeValue) {
            this.nativeValue = nativeValue;
        }
    }

    public static final class BooleanValue extends ConfigValue<Boolean> {
        private BooleanValue(List<String> path, List<String> comments, boolean defaultValue) {
            super(path, comments, defaultValue);
        }
    }

    public static final class IntValue extends ConfigValue<Integer> {
        private final int min;
        private final int max;

        private IntValue(List<String> path, List<String> comments, int defaultValue, int min, int max) {
            super(path, comments, defaultValue);
            this.min = min;
            this.max = max;
        }

        public int min() {
            return min;
        }

        public int max() {
            return max;
        }

        @Override
        public void set(Integer value) {
            super.set(Math.max(min, Math.min(max, value)));
        }
    }

    public static final class DoubleValue extends ConfigValue<Double> {
        private final double min;
        private final double max;

        private DoubleValue(List<String> path, List<String> comments, double defaultValue, double min, double max) {
            super(path, comments, defaultValue);
            this.min = min;
            this.max = max;
        }

        public double min() {
            return min;
        }

        public double max() {
            return max;
        }

        @Override
        public void set(Double value) {
            super.set(Math.max(min, Math.min(max, value)));
        }
    }

    public static final class EnumValue<E extends Enum<E>> extends ConfigValue<E> {
        private EnumValue(List<String> path, List<String> comments, E defaultValue) {
            super(path, comments, defaultValue);
        }
    }
}
