package alpine.nbt;

import java.util.List;
import java.util.Map;

/**
 * The base interface for a Named Binary Tag.
 * <p>
 * A tag can be one of the following types:
 * <ul>
 *     <li>{@code end} — represented by {@link EndTag}</li>
 *     <li>{@code byte} — represented by {@link ByteTag}</li>
 *     <li>{@code short} — represented by {@link ShortTag}</li>
 *     <li>{@code int} — represented by {@link IntTag}</li>
 *     <li>{@code long} — represented by {@link LongTag}</li>
 *     <li>{@code float} — represented by {@link FloatTag}</li>
 *     <li>{@code double} — represented by {@link DoubleTag}</li>
 *     <li>{@code byte array} — represented by {@link ByteArrayTag}</li>
 *     <li>{@code string} — represented by {@link StringTag}</li>
 *     <li>{@code list} — represented by {@link ListTag}</li>
 *     <li>{@code compound} — represented by {@link CompoundTag}</li>
 *     <li>{@code int array} — represented by {@link IntArrayTag}</li>
 *     <li>{@code long array} — represented by {@link LongArrayTag}</li>
 * </ul>
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public sealed interface Tag permits ByteArrayTag, ByteTag, CompoundTag, DoubleTag, EndTag, FloatTag, IntArrayTag, IntTag, ListTag, LongArrayTag, LongTag, ShortTag, StringTag {
    static EndTag end() {
        return EndTag.INSTANCE;
    }

    static ByteTag of(byte value) {
        return new ByteTag(value);
    }

    static ShortTag of(short value) {
        return new ShortTag(value);
    }

    static IntTag of(int value) {
        return new IntTag(value);
    }

    static LongTag of(long value) {
        return new LongTag(value);
    }

    static FloatTag of(float value) {
        return new FloatTag(value);
    }

    static DoubleTag of(double value) {
        return new DoubleTag(value);
    }

    static ByteArrayTag of(byte[] value) {
        return new ByteArrayTag(value);
    }

    static IntArrayTag of(int[] value) {
        return new IntArrayTag(value);
    }

    static LongArrayTag of(long[] value) {
        return new LongArrayTag(value);
    }

    static StringTag of(String value) {
        return new StringTag(value);
    }

    static ListTag list() {
        return new ListTag();
    }

    static ListTag of(List<Tag> tags) {
        return new ListTag(tags);
    }

    static CompoundTag compound() {
        return new CompoundTag();
    }

    static CompoundTag of(Map<String, Tag> tags) {
        return new CompoundTag(tags);
    }

    static TagType type(Tag tag) {
        return TagType.of(tag);
    }
}
