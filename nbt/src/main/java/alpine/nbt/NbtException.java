package alpine.nbt;

/**
 * An exception thrown when the NBT parser encounters malformed input.
 * @author mudkip
 */
public final class NbtException extends Exception {
    public NbtException(String message) {
        super(message);
    }

    public NbtException(String message, Throwable cause) {
        super(message, cause);
    }
}
