package alpine.nbt;

/**
 * A tag which stores a 64-bit floating point number.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public record DoubleTag(double value) implements Tag {

}
