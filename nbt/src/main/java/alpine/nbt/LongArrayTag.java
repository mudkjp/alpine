package alpine.nbt;

import java.util.Arrays;

/**
 * A tag which stores an array of signed 64-bit integers.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public final class LongArrayTag implements Tag {
    private final long[] value;

    LongArrayTag(long[] value) {
        this.value = value.clone();
    }

    public long[] value() {
        return this.value.clone();
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public boolean equals(Object object) {
        return this == object || (object instanceof LongArrayTag tag
                && Arrays.equals(this.value, tag.value));
    }

    @Override
    public String toString() {
        return "long[" + this.value.length + "]";
    }
}
