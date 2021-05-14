package ch.epfl.tchu;

/**
 * A class to check if given conditions are true
 *
 * @author Victor Canard-Duchêne (326913)
 */
public final class Preconditions {
    /**
     * Private constructor to make it impossible to make an instance of this class
     */
    private Preconditions() {
    }

    /**
     * Checks that the given expression is true
     *
     * @param shouldBeTrue : boolean that should be true
     * @throws IllegalArgumentException if the argument isn't true
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
