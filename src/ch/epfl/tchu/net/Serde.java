package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ch.epfl.tchu.net.NetUtils.PATTERN_LIMIT;

/**
 * Represents an object capable of serializing and deserializing values of a given type
 *
 * @param <T> generic type contained in the Serde
 * @author Victor Jean Canard-Duchene (326913)
 */

public interface Serde<T> {
    String emptyString = "";

    /**
     * Static generic method that creates a serde with the serializing and deserializing functions given as arguments.
     *
     * @param serializingFunction   : function to turn an object of type T into a String
     * @param deserializingFunction : function to turn a String into an object of type T
     * @param <T>                   : the type contained in the Serde.
     * @return a new Serde capable of serializing and deserializing objects of generic type T.
     */
    static <T> Serde<T> of(Function<T, String> serializingFunction, Function<String, T> deserializingFunction) {
        return new Serde<>() {
            @Override
            public String serialize(T objectToSerialize) {
                return serializingFunction.apply(objectToSerialize);
            }

            @Override
            public T deserialize(String stringToDeserialize) {
                return deserializingFunction.apply(stringToDeserialize);
            }
        };
    }

    /**
     * Generic static method that creates a serde capable of serializing and deserializing a value of generic type T; which belongs to a
     * list of objects of type T from an enumerated type.
     *
     * @param listOfValuesOfEnumType : values of the enumerated type that have to be turned into a String
     * @param <T>                    : the type contained in the Serde
     * @return a new Serde capable of serializing and deserializing a value T, contained in a specific list of values, of an enumerated type
     */
    static <T> Serde<T> oneOf(List<T> listOfValuesOfEnumType) {
        Preconditions.checkArgument(listOfValuesOfEnumType != null);

        Function<T, String> serializingFunction = (t) -> (t == null) ? emptyString : String.valueOf(listOfValuesOfEnumType.indexOf(t));

        Function<String, T> deserializingFunction = (string) -> (string.equals(emptyString)) ? null : listOfValuesOfEnumType.get(Integer.parseInt(string));

        return of(serializingFunction, deserializingFunction);


    }

    /**
     * Creates a serde capable of (de)serializing the lists of values de(serialized) by the given serde
     *
     * @param usedSerde : a given serde
     * @param delimiter : delimiter separating the components of the string
     * @param <T>       : the type to be contained in the serde
     * @return a Serde of a list of a specified type
     */
    static <T> Serde<List<T>> listOf(Serde<T> usedSerde, String delimiter) {
        Function<List<T>, String> serializingFunction = (list) -> (list.isEmpty()) ? emptyString :
                list
                        .stream()
                        .map(usedSerde::serialize)
                        .collect(Collectors.joining(delimiter));

        Function<String, List<T>> deserializingFunction = (string) -> (string.equals(emptyString)) ? List.of() :
                Arrays.stream(string.split(Pattern.quote(delimiter), PATTERN_LIMIT))
                        .map(usedSerde::deserialize)
                        .collect(Collectors.toList());

        return of(serializingFunction, deserializingFunction);


    }

    /**
     * Creates a serde capable of (de)serializing the sorted bag of values de(serialized) by the given serde
     *
     * @param usedSerde : a given serde
     * @param delimiter : the delimiter separating the components of the string
     * @param <T>       : the type to be contained in the serde
     * @return a Serde of a sorted bag of a specified type
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> usedSerde, String delimiter) {
        Serde<List<T>> listSerde = listOf(usedSerde, delimiter);

        Function<SortedBag<T>, String> serializingFunction = (sortedBag) -> listSerde.serialize(sortedBag.toList());
        Function<String, SortedBag<T>> deserializingFunction = (string) -> SortedBag.of(listSerde.deserialize(string));

        return of(serializingFunction, deserializingFunction);


    }

    /**
     * Abstract method to force each serde of type T to redefine the serializing method, ie how it turns an object of type T into a String.
     *
     * @param objectToSerialize : object of generic type to turn into a string
     * @return the string representing the given object but serialized
     */
    String serialize(T objectToSerialize);

    /**
     * Abstract method to force each serde of type T to redefine the deserializing method, ie how it turns an string (that was converted using
     * the above serializing method) into its corresponding object of type T
     *
     * @param stringToDeserialize : String that was serialized, that needs to be converted back into an object T
     * @return the object that was first serialized into the argument string
     */
    T deserialize(String stringToDeserialize);

}
