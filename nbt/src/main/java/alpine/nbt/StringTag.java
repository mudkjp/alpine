package alpine.nbt;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A tag which stores a sequence of UTF-8 encoded characters.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public record StringTag(String value) implements Tag {
    public StringTag {
        if (value == null) {
            throw new IllegalArgumentException("String value cannot be null!");
        }

        if (value.getBytes(StandardCharsets.UTF_8).length > 0xFFFF) {
            throw new IllegalArgumentException("String value is too long!");
        }
    }

    @Override
    public boolean equals(Object object) {
        return this == object || (object instanceof StringTag tag
                && Objects.equals(this.value, tag.value));
    }
}
