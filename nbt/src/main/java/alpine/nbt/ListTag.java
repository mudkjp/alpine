package alpine.nbt;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A tag which stores an ordered, homogeneous sequence of tags.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public final class ListTag implements Tag, Iterable<Tag> {
    private final List<Tag> tags;

    ListTag() {
        this(List.of());
    }

    ListTag(List<Tag> tags) {
        if (tags == null) {
            throw new IllegalArgumentException("Tags cannot be null!");
        }

        var copy = new ArrayList<Tag>(tags);
        TagType elementType = null;

        for (var tag : copy) {
            if (tag == null) {
                throw new IllegalArgumentException("Tags cannot be null!");
            } else if (tag instanceof EndTag) {
                throw new IllegalArgumentException("NBT lists cannot contain end tags!");
            }

            var type = TagType.of(tag);

            if (elementType == null) {
                elementType = type;
            } else if (elementType != type) {
                throw new IllegalArgumentException("NBT lists must be homogeneous!");
            }
        }

        this.tags = List.copyOf(copy);
    }

    /**
     * Returns the type shared by every element of this list.
     * @return The element type, or {@link TagType#END} if the list is empty.
     */
    public TagType elementType() {
        return this.tags.isEmpty() ? TagType.END : TagType.of(this.tags.getFirst());
    }

    public List<Tag> tags() {
        return this.tags;
    }

    public int length() {
        return this.tags.size();
    }

    public boolean empty() {
        return this.tags.isEmpty();
    }

    public Tag get(int index) {
        return this.tags.get(index);
    }

    public void each(Consumer<Tag> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null!");
        }

        this.tags.forEach(consumer);
    }

    @Override
    public @NotNull Iterator<Tag> iterator() {
        return this.tags.iterator();
    }

    @Override
    public int hashCode() {
        return this.tags.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || (object instanceof ListTag tag
                && Objects.equals(this.tags, tag.tags));
    }

    @Override
    public String toString() {
        return this.tags.toString();
    }
}
