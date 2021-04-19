package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Serde<T> {
    /**
     * Abstract method to force each serde of type T to redefine the serializing method, ie how it turns an object of type T into a String.
     * @param objectToSerialize : object of generic type to turn into a string
     * @return the string representing the given object but serialized
     */
    String serialize(T objectToSerialize);

    /**
     * Abstract method to force each serde of type T to redefine the deserializing method, ie how it turns an string (that was converted using
     * the above serializing method) into its corresponding object of type T
     * @param stringToDeserialize : String that was serialized, that needs to be converted back into an object T
     * @return the object that was first serialized into the argument string
     */
    T deserialize(String stringToDeserialize);


    /**
     * Static generic method that creates a serde with the serializing and deserializing functions given as arguments.
     * @param serializingFunction : function to turn an object of type T into a String
     * @param deserializingFunction : function to turn a String into an object of type T
     * @param <T> : Generic type contained in the Serde.
     * @return a new Serde capable of serializing and deserializing objects of generic type T.
     */
    static <T>Serde<T> of(Function<T, String> serializingFunction, Function<String, T> deserializingFunction){

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
     * @param listOfValuesOfEnumType : values of the enumerated type that have to be turned into a String
     * @param <T> : Generic type contained in the Serde.
     * @return a new Serde capable of serializing and deserializing a value T, contained in a specific list of values, of an enumerated type.
     */
    static <T>Serde<T> oneOf(List<T> listOfValuesOfEnumType){
        Function<T, String> serializingFunction = (t) -> {
            if(t == null){
                return "";
            }
            return String.valueOf(listOfValuesOfEnumType.indexOf(t));
        };

        Function<String, T> deserializingFunction = (string) -> {


                if(string.equals("")){
                    return null;
                }
                return listOfValuesOfEnumType.get(Integer.parseInt(string));
                };

        return of(serializingFunction, deserializingFunction);


    }
    static  <T> Serde<List<T>> listOf(Serde<T> usedSerde, String delimiter){

        Function<List<T>, String> serializingFunction = (list) -> {
            if (list.isEmpty()) {
                return "";
            }
            return

                    list
                            .stream()
                            .map(usedSerde::serialize)
                            .collect(Collectors.joining(delimiter));
        };


        Function<String, List<T>> deserializingFunction = (string) ->{
            if(string.equals("")){
                return List.of();
            }
            String[] splitString = string.split(Pattern.quote(delimiter), -1);

            return Arrays.stream(splitString)
                    .map(usedSerde::deserialize)
                    .collect(Collectors.toList());
        };

        return new Serde<>() {
            @Override
            public String serialize(List<T> objectToSerialize) {
                return serializingFunction.apply(objectToSerialize);
            }

            @Override
            public List<T> deserialize(String stringToDeserialize) {
                return deserializingFunction.apply(stringToDeserialize);
            }
        };


    }
    static  <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> usedSerde, String delimiter){
        Function<SortedBag<T>, String> serializingFunction = (sortedBag) ->
                sortedBag.stream()
                .map(usedSerde::serialize)
                .collect(Collectors.joining(delimiter));

        Function<String, SortedBag<T>> deserializingFunction = (string) ->
                SortedBag.of(Arrays
                        .stream(string
                        .split(Pattern.quote(delimiter), -1))
                .map(usedSerde::deserialize)
                        .collect(Collectors.toList()));

        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> objectToSerialize) {
                return serializingFunction.apply(objectToSerialize);
            }

            @Override
            public SortedBag<T> deserialize(String stringToDeserialize) {
                return deserializingFunction.apply(stringToDeserialize);
            }
        };

    }

}
