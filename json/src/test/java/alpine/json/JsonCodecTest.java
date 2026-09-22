package alpine.json;

import alpine.codec.Codec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static alpine.json.Element.*;
import static org.junit.jupiter.api.Assertions.*;

final class JsonCodecTest {
    record User(String name, int age, List<String> friends) {
        static final Codec<User> CODEC = Codec.<User>builder()
                .with("name", Codec.STRING, User::name)
                .with("age", Codec.INTEGER, User::age)
                .with("friends", Codec.STRING.list(), User::friends)
                .build(User::new);
    }

    enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    record Outer(String outer, Inner inner) {
        public static final Codec<Outer> CODEC = Codec.<Outer>builder()
                .with("outer", Codec.STRING, Outer::outer)
                .with("inner", Inner.CODEC, Outer::inner)
                .build(Outer::new);

        public static final Codec<Outer> FLAT_CODEC = Codec.<Outer>builder()
                .with("outer", Codec.STRING, Outer::outer)
                .with(Inner.CODEC.flatten(), Outer::inner)
                .build(Outer::new);

        record Inner(String inner) {
            public static final Codec<Inner> CODEC = Codec.<Inner>builder()
                    .with("inner", Codec.STRING, Inner::inner)
                    .build(Inner::new);
        }
    }

    @Test
    void testObject() {
        var user = new User("Steve", 67, List.of("Alex"));
        var element = object().set("name", "Steve").set("age", 67).set("friends", array("Alex"));
        assertEquals(element, User.CODEC.encode(JsonTranscoder.INSTANCE, user));
        assertEquals(user, User.CODEC.decode(JsonTranscoder.INSTANCE, element));
    }

    @Test
    void testList() {
        var codec = Codec.INTEGER.list();
        assertEquals(array(1, 2, 3), codec.encode(JsonTranscoder.INSTANCE, List.of(1, 2, 3)));
        assertEquals(List.of(1, 2, 3), codec.decode(JsonTranscoder.INSTANCE, array(1, 2, 3)));
        assertEquals(array(), codec.encode(JsonTranscoder.INSTANCE, List.of()));
        assertEquals(List.of(), codec.decode(JsonTranscoder.INSTANCE, array()));
    }

    @Test
    void testNullable() {
        var codec = Codec.STRING.nullable();
        assertEquals(string("hello"), codec.encode(JsonTranscoder.INSTANCE, "hello"));
        assertEquals(nil(), codec.encode(JsonTranscoder.INSTANCE, null));
        assertEquals("hello", codec.decode(JsonTranscoder.INSTANCE, string("hello")));
        assertNull(codec.decode(JsonTranscoder.INSTANCE, nil()));
    }

    @Test
    void testOptional() {
        var codec = Codec.STRING.optional();
        assertEquals(string("hello"), codec.encode(JsonTranscoder.INSTANCE, Optional.of("hello")));
        assertEquals(nil(), codec.encode(JsonTranscoder.INSTANCE, Optional.empty()));
        assertEquals(Optional.of("hello"), codec.decode(JsonTranscoder.INSTANCE, string("hello")));
        assertEquals(Optional.empty(), codec.decode(JsonTranscoder.INSTANCE, nil()));
    }

    @Test
    void testMap() {
        var codec = Codec.STRING.map(Integer::parseInt, String::valueOf);
        assertEquals(string("42"), codec.encode(JsonTranscoder.INSTANCE, 42));
        assertEquals(42, codec.decode(JsonTranscoder.INSTANCE, string("42")));
    }

    @Test
    void testMapCodec() {
        var codec = Codec.map(Codec.INTEGER);
        var encoded = codec.encode(JsonTranscoder.INSTANCE, Map.of("a", 1, "b", 2));
        assertInstanceOf(ObjectElement.class, encoded);
        var object = (ObjectElement) encoded;
        assertEquals(number(1), object.get("a"));
        assertEquals(number(2), object.get("b"));
        assertEquals(Map.of("a", 1, "b", 2), codec.decode(JsonTranscoder.INSTANCE, encoded));
    }

    @Test
    void testPrimitive() {
        assertEquals(bool(true), Codec.BOOLEAN.encode(JsonTranscoder.INSTANCE, true));
        assertEquals(true, Codec.BOOLEAN.decode(JsonTranscoder.INSTANCE, bool(true)));

        assertEquals(number(127), Codec.BYTE.encode(JsonTranscoder.INSTANCE, (byte) 127));
        assertEquals((byte) 127, Codec.BYTE.decode(JsonTranscoder.INSTANCE, number(127)));

        assertEquals(number(1000), Codec.SHORT.encode(JsonTranscoder.INSTANCE, (short) 1000));
        assertEquals((short) 1000, Codec.SHORT.decode(JsonTranscoder.INSTANCE, number(1000)));

        assertEquals(number(42), Codec.INTEGER.encode(JsonTranscoder.INSTANCE, 42));
        assertEquals(42, Codec.INTEGER.decode(JsonTranscoder.INSTANCE, number(42)));

        assertEquals(number(123456789L), Codec.LONG.encode(JsonTranscoder.INSTANCE, 123456789L));
        assertEquals(123456789L, Codec.LONG.decode(JsonTranscoder.INSTANCE, number(123456789)));

        assertEquals(number(1.5f), Codec.FLOAT.encode(JsonTranscoder.INSTANCE, 1.5f));
        assertEquals(1.5f, Codec.FLOAT.decode(JsonTranscoder.INSTANCE, number(1.5)));

        assertEquals(number(3.14), Codec.DOUBLE.encode(JsonTranscoder.INSTANCE, 3.14));
        assertEquals(3.14, Codec.DOUBLE.decode(JsonTranscoder.INSTANCE, number(3.14)));

        assertEquals(string("hi"), Codec.STRING.encode(JsonTranscoder.INSTANCE, "hi"));
        assertEquals("hi", Codec.STRING.decode(JsonTranscoder.INSTANCE, string("hi")));

        assertEquals(string("Z"), Codec.CHARACTER.encode(JsonTranscoder.INSTANCE, 'Z'));
        assertEquals('Z', Codec.CHARACTER.decode(JsonTranscoder.INSTANCE, string("Z")));
    }

    @Test
    void testArray() {
        var codec = Codec.STRING.array(String[]::new);
        assertEquals(array("a", "b", "c"), codec.encode(JsonTranscoder.INSTANCE, new String[] {"a", "b", "c"}));
        assertArrayEquals(new String[] {"x", "y"}, codec.decode(JsonTranscoder.INSTANCE, array("x", "y")));
    }

    @Test
    void testPrimitiveArray() {
        var booleans = new boolean[] {true, false, true};
        assertArrayEquals(booleans, Codec.BOOLEAN_ARRAY.decode(JsonTranscoder.INSTANCE, Codec.BOOLEAN_ARRAY.encode(JsonTranscoder.INSTANCE, booleans)));

        var integers = new int[] {1, 2, 3};
        assertArrayEquals(integers, Codec.INTEGER_ARRAY.decode(JsonTranscoder.INSTANCE, Codec.INTEGER_ARRAY.encode(JsonTranscoder.INSTANCE, integers)));

        var longs = new long[] {100L, 200L};
        assertArrayEquals(longs, Codec.LONG_ARRAY.decode(JsonTranscoder.INSTANCE, Codec.LONG_ARRAY.encode(JsonTranscoder.INSTANCE, longs)));

        var doubles = new double[] {1.1, 2.2};
        assertArrayEquals(doubles, Codec.DOUBLE_ARRAY.decode(JsonTranscoder.INSTANCE, Codec.DOUBLE_ARRAY.encode(JsonTranscoder.INSTANCE, doubles)));
    }

    @Test
    void testEnumName() {
        var codec = Codec.name(Direction.class);
        assertEquals(string("north"), codec.encode(JsonTranscoder.INSTANCE, Direction.NORTH));
        assertEquals(Direction.SOUTH, codec.decode(JsonTranscoder.INSTANCE, string("south")));

        for (var direction : Direction.values()) {
            assertEquals(direction, codec.decode(JsonTranscoder.INSTANCE, codec.encode(JsonTranscoder.INSTANCE, direction)));
        }

        assertThrows(Exception.class, () -> codec.decode(JsonTranscoder.INSTANCE, string("up")));
    }

    @Test
    void testEnumOrdinal() {
        var codec = Codec.ordinal(Direction.class);
        assertEquals(number(0), codec.encode(JsonTranscoder.INSTANCE, Direction.NORTH));
        assertEquals(Direction.SOUTH, codec.decode(JsonTranscoder.INSTANCE, number(1)));

        for (var direction : Direction.values()) {
            assertEquals(direction, codec.decode(JsonTranscoder.INSTANCE, codec.encode(JsonTranscoder.INSTANCE, direction)));
        }

        assertThrows(Exception.class, () -> codec.decode(JsonTranscoder.INSTANCE, number(999)));
    }

    @Test
    void testUuid() {
        var uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        assertEquals(string("550e8400-e29b-41d4-a716-446655440000"), Codec.UUID.encode(JsonTranscoder.INSTANCE, uuid));
        assertEquals(uuid, Codec.UUID.decode(JsonTranscoder.INSTANCE, string("550e8400-e29b-41d4-a716-446655440000")));
        var random = UUID.randomUUID();
        assertEquals(random, Codec.UUID.decode(JsonTranscoder.INSTANCE, Codec.UUID.encode(JsonTranscoder.INSTANCE, random)));
    }

    @Test
    void testFlatten() {
        var value = new Outer("outer", new Outer.Inner("inner"));
        assertEquals(object().set("outer", "outer").set("inner", object().set("inner", "inner")), Outer.CODEC.encode(JsonTranscoder.INSTANCE, value));
        assertEquals(object().set("outer", "outer").set("inner", "inner"), Outer.FLAT_CODEC.encode(JsonTranscoder.INSTANCE, value));
    }
}
