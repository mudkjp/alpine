package alpine.nbt;

import org.jetbrains.annotations.ApiStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

@ApiStatus.Internal
final class NbtReader {
    private byte[] input;
    private int position;

    Tag read(byte[] input) throws NbtException {
        this.input = input;
        this.position = 0;
        var root = this.readNamedTag();

        if (this.position < input.length) {
            throw new NbtException("Unexpected trailing content after root tag!");
        }

        return root.tag();
    }

    private NamedTag readNamedTag() throws NbtException {
        var type = Byte.toUnsignedInt(this.readByte());

        if (type == TagType.END.id()) {
            return new NamedTag("", Tag.end());
        }

        var name = this.readString();
        return new NamedTag(name, this.readPayload(type));
    }

    private Tag readPayload(int type) throws NbtException {
        return switch (TagType.fromId(type)) {
            case END -> Tag.end();
            case BYTE -> Tag.of(this.readByte());
            case SHORT -> Tag.of(this.readShort());
            case INT -> Tag.of(this.readInt());
            case LONG -> Tag.of(this.readLong());
            case FLOAT -> Tag.of(this.readFloat());
            case DOUBLE -> Tag.of(this.readDouble());
            case BYTE_ARRAY -> {
                var length = this.readLength("byte array");
                yield Tag.of(this.readBytes(length));
            }
            case STRING -> Tag.of(this.readString());
            case LIST -> this.readList();
            case COMPOUND -> this.readCompound();
            case INT_ARRAY -> {
                var length = this.readLength("int array");
                var values = new int[length];

                for (var index = 0; index < length; index++) {
                    values[index] = this.readInt();
                }

                yield Tag.of(values);
            }
            case LONG_ARRAY -> {
                var length = this.readLength("long array");
                var values = new long[length];

                for (var index = 0; index < length; index++) {
                    values[index] = this.readLong();
                }

                yield Tag.of(values);
            }
        };
    }

    private ListTag readList() throws NbtException {
        var elementType = Byte.toUnsignedInt(this.readByte());
        var length = this.readLength("list");

        if (length > 0 && elementType == TagType.END.id()) {
            throw new NbtException("List with elements must declare an element type!");
        }

        var tags = new ArrayList<Tag>(length);

        for (var index = 0; index < length; index++) {
            tags.add(this.readPayload(elementType));
        }

        return Tag.of(tags);
    }

    private CompoundTag readCompound() throws NbtException {
        var tags = new LinkedHashMap<String, Tag>();

        while (true) {
            var named = this.readNamedTag();

            if (named.tag() instanceof EndTag) {
                break;
            }

            tags.put(named.name(), named.tag());
        }

        return Tag.of(tags);
    }

    private String readString() throws NbtException {
        var length = Short.toUnsignedInt(this.readShort());
        return new String(this.readBytes(length), StandardCharsets.UTF_8);
    }

    private int readLength(String description) throws NbtException {
        var length = this.readInt();

        if (length < 0) {
            throw new NbtException("Negative " + description + " length: " + length);
        }

        return length;
    }

    private byte readByte() throws NbtException {
        this.require(1);
        return this.input[this.position++];
    }

    private short readShort() throws NbtException {
        this.require(2);
        var value = (short) (((this.input[this.position] & 0xFF) << 8)
                | (this.input[this.position + 1] & 0xFF));
        this.position += 2;
        return value;
    }

    private int readInt() throws NbtException {
        this.require(4);
        var value = ((this.input[this.position] & 0xFF) << 24)
                | ((this.input[this.position + 1] & 0xFF) << 16)
                | ((this.input[this.position + 2] & 0xFF) << 8)
                | (this.input[this.position + 3] & 0xFF);
        this.position += 4;
        return value;
    }

    private long readLong() throws NbtException {
        this.require(8);
        var value = ((long) (this.input[this.position] & 0xFF) << 56)
                | ((long) (this.input[this.position + 1] & 0xFF) << 48)
                | ((long) (this.input[this.position + 2] & 0xFF) << 40)
                | ((long) (this.input[this.position + 3] & 0xFF) << 32)
                | ((long) (this.input[this.position + 4] & 0xFF) << 24)
                | ((long) (this.input[this.position + 5] & 0xFF) << 16)
                | ((long) (this.input[this.position + 6] & 0xFF) << 8)
                | (this.input[this.position + 7] & 0xFF);
        this.position += 8;
        return value;
    }

    private float readFloat() throws NbtException {
        return Float.intBitsToFloat(this.readInt());
    }

    private double readDouble() throws NbtException {
        return Double.longBitsToDouble(this.readLong());
    }

    private byte[] readBytes(int length) throws NbtException {
        this.require(length);
        var value = Arrays.copyOfRange(this.input, this.position, this.position + length);
        this.position += length;
        return value;
    }

    private void require(int length) throws NbtException {
        if (this.position + length > this.input.length) {
            throw new NbtException("Unexpected end of input!");
        }
    }
}
