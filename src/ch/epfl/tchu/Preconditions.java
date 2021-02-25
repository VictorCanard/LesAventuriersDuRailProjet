package ch.epfl.tchu;

public final class Preconditions {
    /**
     * Private constructor to make it impossible to make an instance of this class
     */
    private Preconditions(){

    }

    /**
     * Throws an IllegalArgumentException if the argument isn't true
     * @param shouldBeTrue
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
