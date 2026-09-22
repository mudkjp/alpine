package alpine.nbt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A helper class for reading and writing NBT data.
 * @see <a href="https://minecraft.wiki/w/NBT_format">NBT format</a>
 * @author mudkip
 */
public final class Nbt {
    private static final NbtReader READER = new NbtReader();
    private static final NbtWriter WRITER = new NbtWriter();

    private Nbt() {

    }

    public static CompoundTag read(byte[] data) throws NbtException {
        var tag = READER.read(isCompressed(data) ? decompress(data) : data);

        if (tag instanceof CompoundTag compound) {
            return compound;
        }

        throw new NbtException("Root tag is not a compound!");
    }

    public static CompoundTag read(InputStream stream) throws IOException, NbtException {
        return read(stream.readAllBytes());
    }

    public static CompoundTag read(Path path) throws NbtException {
        try {
            return read(Files.readAllBytes(path));
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to read NBT from file!", exception);
        }
    }

    public static CompoundTag read(File file) throws NbtException {
        return read(file.toPath());
    }

    public static byte[] write(Tag tag) {
        return WRITER.write(tag);
    }

    public static byte[] writeCompressed(Tag tag) {
        return compress(write(tag));
    }

    public static void write(OutputStream stream, Tag tag) throws IOException {
        WRITER.write(stream, tag);
    }

    public static void writeCompressed(OutputStream stream, Tag tag) throws IOException {
        try (var output = new GZIPOutputStream(stream)) {
            WRITER.write(output, tag);
        }
    }

    public static void write(Path path, Tag tag, boolean compressed) throws IOException {
        if (compressed) {
            try (var output = Files.newOutputStream(path)) {
                writeCompressed(output, tag);
            }
        } else {
            Files.write(path, write(tag));
        }
    }

    public static void write(File file, Tag tag, boolean compressed) throws IOException {
        write(file.toPath(), tag, compressed);
    }

    private static boolean isCompressed(byte[] data) {
        return data.length >= 2
                && (data[0] & 0xFF) == 0x1F
                && (data[1] & 0xFF) == 0x8B;
    }

    private static byte[] decompress(byte[] data) throws NbtException {
        try (var input = new GZIPInputStream(new ByteArrayInputStream(data))) {
            return input.readAllBytes();
        } catch (IOException exception) {
            throw new NbtException("Failed to decompress NBT data!", exception);
        }
    }

    private static byte[] compress(byte[] data) {
        var buffer = new ByteArrayOutputStream();

        try (var output = new GZIPOutputStream(buffer)) {
            output.write(data);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        return buffer.toByteArray();
    }
}
