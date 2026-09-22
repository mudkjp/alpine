package alpine.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A tag which stores a collection of key-value pairs where the keys are strings.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public final class CompoundTag implements Tag, Iterable<Map.Entry<String, Tag>> {
    private final Map<String, Tag> tags;

    CompoundTag() {
        this(Map.of());
    }

    CompoundTag(Map<String, Tag> tags) {
        if (tags == null) {
            throw new IllegalArgumentException("Tags cannot be null!");
        }

        var copy = new LinkedHashMap<String, Tag>();

        tags.forEach((key, value) -> {
            if (key == null) {
                throw new IllegalArgumentException("Keys cannot be null!");
            } else if (value == null || value instanceof EndTag) {
                throw new IllegalArgumentException("Values cannot be absent!");
            }

            copy.put(key, value);
        });

        this.tags = Collections.unmodifiableMap(copy);
    }

    public int length() {
        return this.tags.size();
    }

    public boolean empty() {
        return this.tags.isEmpty();
    }

    public boolean has(String key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null!");
        return this.tags.containsKey(key);
    }

    public @Nullable Tag get(String key) {
        return this.tags.get(key);
    }

    public @Nullable Tag get(String key, Tag fallback) {
        return this.tags.getOrDefault(key, fallback);
    }

    public @Nullable String getString(String key) {
        return this.tags.get(key) instanceof StringTag tag ? tag.value() : null;
    }

    public String getString(String key, String fallback) {
        return this.tags.get(key) instanceof StringTag tag ? tag.value() : fallback;
    }

    public byte getByte(String key, byte fallback) {
        return this.tags.get(key) instanceof ByteTag tag ? tag.value() : fallback;
    }

    public short getShort(String key, short fallback) {
        return this.tags.get(key) instanceof ShortTag tag ? tag.value() : fallback;
    }

    public int getInteger(String key, int fallback) {
        return this.tags.get(key) instanceof IntegerTag tag ? tag.value() : fallback;
    }

    public long getLong(String key, long fallback) {
        return this.tags.get(key) instanceof LongTag tag ? tag.value() : fallback;
    }

    public float getFloat(String key, float fallback) {
        return this.tags.get(key) instanceof FloatTag tag ? tag.value() : fallback;
    }

    public double getDouble(String key, double fallback) {
        return this.tags.get(key) instanceof DoubleTag tag ? tag.value() : fallback;
    }

    public boolean getBoolean(String key, boolean fallback) {
        return this.tags.get(key) instanceof ByteTag tag ? tag.value() != 0 : fallback;
    }

    public @Nullable ListTag getList(String key) {
        return this.tags.get(key) instanceof ListTag tag ? tag : null;
    }

    public @Nullable CompoundTag getCompound(String key) {
        return this.tags.get(key) instanceof CompoundTag tag ? tag : null;
    }

    public void each(BiConsumer<String, Tag> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null!");
        }

        this.tags.forEach(consumer);
    }

    public CompoundTag set(String key, Tag value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        } else if (value == null || value instanceof EndTag) {
            throw new IllegalArgumentException("Value cannot be absent!");
        }

        var copy = new LinkedHashMap<>(this.tags);
        copy.put(key, value);
        return new CompoundTag(copy);
    }

    public CompoundTag set(String key, boolean value) {
        return this.set(key, Tag.of((byte) (value ? 1 : 0)));
    }

    public CompoundTag set(String key, byte value) {
        return this.set(key, Tag.of(value));
    }

    public CompoundTag set(String key, short value) {
        return this.set(key, Tag.of(value));
    }

    public CompoundTag set(String key, int value) {
        return this.set(key, Tag.of(value));
    }

    public CompoundTag set(String key, long value) {
        return this.set(key, Tag.of(value));
    }

    public CompoundTag set(String key, float value) {
        return this.set(key, Tag.of(value));
    }

    public CompoundTag set(String key, double value) {
        return this.set(key, Tag.of(value));
    }

    public CompoundTag set(String key, String value) {
        return this.set(key, Tag.of(value));
    }

    public CompoundTag remove(String key) {
        if (!this.tags.containsKey(key)) {
            throw new IllegalStateException("Key \"" + key + "\" is not present!");
        }

        var copy = new LinkedHashMap<>(this.tags);
        copy.remove(key);
        return new CompoundTag(copy);
    }

    @Override
    public @NotNull Iterator<Map.Entry<String, Tag>> iterator() {
        return this.tags.entrySet().iterator();
    }

    @Override
    public int hashCode() {
        return this.tags.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || (object instanceof CompoundTag tag
                && Objects.equals(this.tags, tag.tags));
    }

    @Override
    public String toString() {
        return this.tags.toString();
    }
}
