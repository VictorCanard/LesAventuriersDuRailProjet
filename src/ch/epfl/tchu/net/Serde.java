package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Serde<T> {
    String serialize(T objectToSerialize);

    T deserialize(String stringToDeserialize);

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
    static <T>Serde<T> oneOf(List<T> listOfValuesOfEnumType){
        Function<T, String> serializingFunction = (t) -> String.valueOf(listOfValuesOfEnumType.indexOf(t));

        Function<String, T> deserializingFunction = (string) -> listOfValuesOfEnumType.get(Integer.parseInt(string));

        return of(serializingFunction, deserializingFunction);


    }
    static  <T> Serde<List<T>> listOf(Serde<T> usedSerde, String delimiter){

        Function<List<T>, String> serializingFunction = (list) -> new StringJoiner(delimiter)
                .add(
                        list.stream()
                                .map(usedSerde::serialize)
                                .collect(Collectors.toList()).toString())
                                .toString();


        Function<String, List<T>> deserializingFunction = (string) ->
                Arrays.stream(string
                .split(Pattern.quote(delimiter), -1))
                .map(usedSerde::deserialize)
                .collect(Collectors.toList());

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
                new StringJoiner(delimiter)
                .add(
                        sortedBag.stream()
                                .map(usedSerde::serialize)
                                .collect(Collectors.toList()).toString())
                .toString();


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
