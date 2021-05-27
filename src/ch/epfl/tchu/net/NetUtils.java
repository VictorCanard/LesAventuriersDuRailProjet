package ch.epfl.tchu.net;

import java.util.Arrays;
import java.util.Iterator;


/**
 * Class for all utilities common to multiple classes in the net package.
 * @author : Victor Canard-Duchêne (326913)
 */
class NetUtils {
    final static int patternLimit = -1;

    static Iterator<String> getStringIterator(String next, String patternDelimiter) {
        String[] playerNamesSerialized = next.split(patternDelimiter, patternLimit);
        return Arrays.stream(playerNamesSerialized).iterator();
    }
}
