package alpine.nbt;

import alpine.codec.DecodingException;
import alpine.codec.Transcoder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link Transcoder} which encodes and decodes {@link Tag NBT tags}.
 * <p>
 * Because NBT is strongly typed and the codec API only knows about a single
 * number type, {@link #encodeNumber(double)} chooses the smallest integer tag
 * which can represent the value and falls back to a {@link DoubleTag} otherwise.
 * @author mudkip
 */
public final class NbtTranscoder implements Transcoder<Tag> {
    public static final NbtTranscoder INSTANCE = new NbtTranscoder();

    private NbtTranscoder() {

    }

    @Override
    public boolean isNull(Tag value) {
        return value == null || value instanceof EndTag;
    }

    @Override
    public Tag encodeNull() {
        return Tag.end();
    }

    @Override
    public boolean decodeBoolean(Tag value) {
        if (value instanceof ByteTag tag) {
            return tag.value() != 0;
        } else {
            throw new DecodingException("Expected a byte tag!");
        }
    }

    @Override
    public Tag encodeBoolean(boolean value) {
        return Tag.of((byte) (value ? 1 : 0));
    }

    @Override
    public double decodeNumber(Tag value) {
        return switch (value) {
            case ByteTag tag -> tag.value();
            case ShortTag tag -> tag.value();
            case IntTag tag -> tag.value();
            case LongTag tag -> tag.value();
            case FloatTag tag -> tag.value();
            case DoubleTag tag -> tag.value();
            default -> throw new DecodingException("Expected a numeric tag!");
        };
    }

    @Override
    public Tag encodeNumber(double value) {
        if (value == Math.rint(value)) {
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                return Tag.of((int) value);
            } else if (value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) {
                return Tag.of((long) value);
            }
        }

        return Tag.of(value);
    }

    @Override
    public String decodeString(Tag value) {
        if (value instanceof StringTag tag) {
            return tag.value();
        } else {
            throw new DecodingException("Expected a string tag!");
        }
    }

    @Override
    public Tag encodeString(String value) {
        return Tag.of(value);
    }

    @Override
    public List<Tag> decodeArray(Tag value) {
        if (value instanceof ListTag tag) {
            return tag.tags();
        } else {
            throw new DecodingException("Expected a list tag!");
        }
    }

    @Override
    public Tag encodeArray(List<Tag> elements) {
        return Tag.of(elements);
    }

    @Override
    public Map<String, Tag> decodeObject(Tag value) {
        if (value instanceof CompoundTag tag) {
            var tags = new LinkedHashMap<String, Tag>();
            tag.each(tags::put);
            return tags;
        } else {
            throw new DecodingException("Expected a compound tag!");
        }
    }

    @Override
    public Tag decodeObjectField(Tag object, String key) {
        if (object instanceof CompoundTag tag) {
            return tag.get(key);
        } else {
            throw new DecodingException("Expected a compound tag!");
        }
    }

    @Override
    public Tag encodeObject(Map<String, Tag> fields) {
        var tags = new LinkedHashMap<String, Tag>();

        fields.forEach((key, value) -> {
            if (value != null && !(value instanceof EndTag)) {
                tags.put(key, value);
            }
        });

        return Tag.of(tags);
    }
}
