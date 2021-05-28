package ch.epfl.tchu.net;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Class for all utilities common to multiple classes in the net package.
 * @author : Victor Canard-Duchêne (326913)
 */
class NetUtils {
    private NetUtils(){}

    /**
     * The number of times the pattern is applied in the split method.
     * -1 to be applied as many times as possible
     */
    static final int PATTERN_LIMIT = -1;

    /**
     * Creates an iterator from a given string that will be split up
     * @param next : the string to be split
     * @param patternDelimiter : the pattern the split will be based on
     * @return an iterator of type String
     */
    static Iterator<String> getStringIterator(String next, String patternDelimiter) {
        String[] playerNamesSerialized = next.split(patternDelimiter, PATTERN_LIMIT);
        return Arrays.stream(playerNamesSerialized).iterator();
    }
}
