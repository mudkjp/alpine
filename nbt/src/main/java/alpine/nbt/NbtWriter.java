package alpine.nbt;

import org.jetbrains.annotations.ApiStatus;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ApiStatus.Internal
final class NbtWriter {
    byte[] write(Tag tag) {
        var buffer = new ByteArrayOutputStream();

        try (var output = new DataOutputStream(buffer)) {
            this.writeTag(output, tag, "");
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        return buffer.toByteArray();
    }

    void write(OutputStream stream, Tag tag) throws IOException {
        var output = new DataOutputStream(stream);
        this.writeTag(output, tag, "");
        output.flush();
    }

    private void writeTag(DataOutputStream output, Tag tag, String name) throws IOException {
        output.writeByte(TagType.of(tag).id());

        if (!(tag instanceof EndTag)) {
            this.writeString(output, name);
        }

        this.writePayload(output, tag);
    }

    private void writePayload(DataOutputStream output, Tag tag) throws IOException {
        switch (tag) {
            case EndTag _ -> {
            }

            case ByteTag value -> output.writeByte(value.value());
            case ShortTag value -> output.writeShort(value.value());
            case IntTag value -> output.writeInt(value.value());
            case LongTag value -> output.writeLong(value.value());
            case FloatTag value -> output.writeFloat(value.value());
            case DoubleTag value -> output.writeDouble(value.value());

            case ByteArrayTag value -> {
                var bytes = value.value();
                output.writeInt(bytes.length);
                output.write(bytes);
            }

            case StringTag value -> this.writeString(output, value.value());

            case ListTag value -> {
                output.writeByte(value.elementType().id());
                output.writeInt(value.length());

                for (var element : value) {
                    this.writePayload(output, element);
                }
            }

            case CompoundTag value -> {
                for (var entry : value) {
                    this.writeTag(output, entry.getValue(), entry.getKey());
                }

                output.writeByte(TagType.END.id());
            }

            case IntArrayTag value -> {
                var values = value.value();
                output.writeInt(values.length);

                for (var item : values) {
                    output.writeInt(item);
                }
            }

            case LongArrayTag value -> {
                var values = value.value();
                output.writeInt(values.length);

                for (var item : values) {
                    output.writeLong(item);
                }
            }
        }
    }

    private void writeString(DataOutputStream output, String value) throws IOException {
        var bytes = value.getBytes(StandardCharsets.UTF_8);

        if (bytes.length > 0xFFFF) {
            throw new IllegalArgumentException("String value is too long!");
        }

        output.writeShort(bytes.length);
        output.write(bytes);
    }
}
