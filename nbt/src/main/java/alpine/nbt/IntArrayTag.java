package alpine.nbt;

import java.util.Arrays;

/**
 * A tag which stores an array of signed 32-bit integers.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public final class IntArrayTag implements Tag {
    private final int[] value;

    IntArrayTag(int[] value) {
        this.value = value.clone();
    }

    public int[] value() {
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
        return this == object || (object instanceof IntArrayTag tag
                && Arrays.equals(this.value, tag.value));
    }

    @Override
    public String toString() {
        return "int[" + this.value.length + "]";
    }
}
