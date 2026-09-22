package alpine.nbt;

import alpine.codec.Codec;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class NbtTest {
    @Test
    void testReadSimpleInt() throws NbtException {
        var data = new byte[] {
                0x0A, // compound
                0x00, 0x00, // name ""
                0x03, // int
                0x00, 0x01, 0x78, // name "x"
                0x00, 0x00, 0x00, 0x05, // value 5
                0x00 // end
        };

        assertEquals(Tag.compound().set("x", 5), Nbt.read(data));
    }

    @Test
    void testRoundTripAllTags() throws NbtException {
        var compound = Tag.compound()
                .set("byte", (byte) 127)
                .set("short", (short) 1000)
                .set("int", 42)
                .set("long", 123456789L)
                .set("float", 1.5F)
                .set("double", 3.14D)
                .set("boolean", true)
                .set("string", "hello")
                .set("byteArray", Tag.of(new byte[] {1, 2, 3}))
                .set("intArray", Tag.of(new int[] {4, 5, 6}))
                .set("longArray", Tag.of(new long[] {7L, 8L, 9L}))
                .set("list", Tag.of(List.of(Tag.of("a"), Tag.of("b"))))
                .set("nested", Tag.compound().set("inner", 1));

        assertEquals(compound, Nbt.read(Nbt.write(compound)));
    }

    @Test
    void testCompressedRoundTrip() throws NbtException {
        var compound = Tag.compound().set("x", 5);
        var data = Nbt.writeCompressed(compound);

        assertNotEquals(0x0A, data[0] & 0xFF, "data should not be raw NBT");
        assertEquals(compound, Nbt.read(data));
    }

    @Test
    void testEmptyListRoundTrip() throws NbtException {
        var compound = Tag.compound().set("list", Tag.list());
        assertEquals(compound, Nbt.read(Nbt.write(compound)));
    }

    @Test
    void testEmptyCompound() throws NbtException {
        var compound = Tag.compound();
        assertEquals(compound, Nbt.read(Nbt.write(compound)));
    }

    @Test
    void testHeterogeneousListRejected() {
        assertThrows(IllegalArgumentException.class, () -> Tag.of(List.of(Tag.of(1), Tag.of("a"))));
    }

    @Test
    void testTruncatedInput() {
        var data = new byte[] {0x0A, 0x00, 0x00, 0x03};
        assertThrows(NbtException.class, () -> Nbt.read(data));
    }

    @Test
    void testRootNotCompound() {
        var data = new byte[] {0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05};
        assertThrows(NbtException.class, () -> Nbt.read(data));
    }

    record Player(String name, int level, List<String> effects) {
        static final Codec<Player> CODEC = Codec.<Player>builder()
                .with("name", Codec.STRING, Player::name)
                .with("level", Codec.INTEGER, Player::level)
                .with("effects", Codec.STRING.list(), Player::effects)
                .build(Player::new);
    }

    @Test
    void testCodec() {
        var player = new Player("Steve", 42, List.of("speed", "haste"));
        var encoded = Player.CODEC.encode(NbtTranscoder.INSTANCE, player);

        assertInstanceOf(CompoundTag.class, encoded);
        assertEquals("Steve", ((CompoundTag) encoded).getString("name"));
        assertEquals(player, Player.CODEC.decode(NbtTranscoder.INSTANCE, encoded));
    }

    @Test
    void testCodecNullable() {
        var codec = Codec.STRING.nullable();

        assertEquals(Tag.end(), codec.encode(NbtTranscoder.INSTANCE, null));
        assertNull(codec.decode(NbtTranscoder.INSTANCE, Tag.end()));
        assertEquals("hello", codec.decode(NbtTranscoder.INSTANCE, Tag.of("hello")));
    }

    @Test
    void testCodecPrimitives() {
        assertEquals(Tag.of(42), Codec.INTEGER.encode(NbtTranscoder.INSTANCE, 42));
        assertEquals(42, Codec.INTEGER.decode(NbtTranscoder.INSTANCE, Tag.of(42)));

        assertEquals(Tag.of(1.5D), Codec.FLOAT.encode(NbtTranscoder.INSTANCE, 1.5F));
        assertEquals(1.5F, Codec.FLOAT.decode(NbtTranscoder.INSTANCE, Tag.of(1.5F)));
        assertEquals(1.5F, Codec.FLOAT.decode(NbtTranscoder.INSTANCE, Tag.of(1.5D)));

        assertEquals(Tag.of((byte) 1), Codec.BOOLEAN.encode(NbtTranscoder.INSTANCE, true));
        assertTrue(Codec.BOOLEAN.decode(NbtTranscoder.INSTANCE, Tag.of((byte) 1)));
    }
}
