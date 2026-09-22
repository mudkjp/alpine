package alpine.nbt;

/**
 * The type of a {@link Tag} in the Named Binary Tag format.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public enum TagType {
    END(0),
    BYTE(1),
    SHORT(2),
    INT(3),
    LONG(4),
    FLOAT(5),
    DOUBLE(6),
    BYTE_ARRAY(7),
    STRING(8),
    LIST(9),
    COMPOUND(10),
    INT_ARRAY(11),
    LONG_ARRAY(12);

    private static final TagType[] BY_ID = new TagType[values().length];

    static {
        for (var type : values()) {
            BY_ID[type.id] = type;
        }
    }

    private final int id;

    TagType(int id) {
        this.id = id;
    }

    public int id() {
        return this.id;
    }

    public static TagType fromId(int id) {
        if (id < 0 || id >= BY_ID.length) {
            throw new IllegalArgumentException("Unknown tag type id: " + id);
        }

        return BY_ID[id];
    }

    public static TagType of(Tag tag) {
        return switch (tag) {
            case EndTag _ -> END;
            case ByteTag _ -> BYTE;
            case ShortTag _ -> SHORT;
            case IntegerTag _ -> INT;
            case LongTag _ -> LONG;
            case FloatTag _ -> FLOAT;
            case DoubleTag _ -> DOUBLE;
            case ByteArrayTag _ -> BYTE_ARRAY;
            case StringTag _ -> STRING;
            case ListTag _ -> LIST;
            case CompoundTag _ -> COMPOUND;
            case IntArrayTag _ -> INT_ARRAY;
            case LongArrayTag _ -> LONG_ARRAY;
        };
    }
}
