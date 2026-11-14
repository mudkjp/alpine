package alpine.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static alpine.json.Element.*;
import static org.junit.jupiter.api.Assertions.*;

final class ElementImmutabilityTest {
    @Test
    void testArrayIteratorIsUnmodifiable() {
        var iterator = array(1, 2, 3).iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void testObjectIteratorIsUnmodifiable() {
        var iterator = object().set("a", 1).iterator();
        var entry = iterator.next();
        assertThrows(UnsupportedOperationException.class, () -> entry.setValue(string("b")));
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void testObjectStreamIsUnmodifiable() {
        var entry = object().set("a", 1).stream().findFirst().orElseThrow();
        assertThrows(UnsupportedOperationException.class, () -> entry.setValue(string("b")));
    }

    @Test
    void testArrayConstructorCopies() {
        var list = new ArrayList<Element>();
        var element = new ArrayElement(list);
        list.add(string("a"));
        assertTrue(element.empty());
    }

    @Test
    void testObjectConstructorCopies() {
        var map = new LinkedHashMap<String, Element>();
        var element = new ObjectElement(map);
        map.put("a", string("b"));
        assertTrue(element.empty());
    }
}
