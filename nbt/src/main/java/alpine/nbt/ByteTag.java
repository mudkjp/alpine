package alpine.nbt;

/**
 * A tag which stores a signed 8-bit integer.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public record ByteTag(byte value) implements Tag {

}
