package alpine.nbt;

/**
 * A tag with no payload which terminates a {@link CompoundTag}.
 * <p>
 * It is also used by {@link NbtTranscoder} to represent the absence of a value.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public final class EndTag implements Tag {
    public static final EndTag INSTANCE = new EndTag();

    private EndTag() {

    }

    @Override
    public String toString() {
        return "END";
    }
}
