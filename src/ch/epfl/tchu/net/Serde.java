package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Serde<U> {
    String serialize(U objectUoSerialize);

    U deserialize(String stringUoDeserialize);

    default Serde<U> of(Function<U, String> serializingFunction, Function<String, U> deserializingFunction){

        return new Serde<>() {
            @Override
            public String serialize(U objectUoSerialize) {
                return serializingFunction.apply(objectUoSerialize);
            }

            @Override
            public U deserialize(String stringUoDeserialize) {
                return deserializingFunction.apply(stringUoDeserialize);
            }
        };
    }
    default Serde<U> oneOf(List<U> listOfValuesOfEnumType){
        Function<U, String> serializingFunction = (t) -> String.valueOf(listOfValuesOfEnumType.indexOf(t));

        Function<String, U> deserializingFunction = (string) -> listOfValuesOfEnumType.get(Integer.parseInt(string));

        return of(serializingFunction, deserializingFunction);


    }
    default <T> Serde<List<T>> listOf(Serde<T> usedSerde, String delimiter){
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
            public String serialize(List<T> objectUoSerialize) {
                return serializingFunction.apply(objectUoSerialize);
            }

            @Override
            public List<T> deserialize(String stringUoDeserialize) {
                return deserializingFunction.apply(stringUoDeserialize);
            }
        };


    }
    default <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> usedSerde, String delimiter){
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
            public String serialize(SortedBag<T> objectUoSerialize) {
                return serializingFunction.apply(objectUoSerialize);
            }

            @Override
            public SortedBag<T> deserialize(String stringUoDeserialize) {
                return deserializingFunction.apply(stringUoDeserialize);
            }
        };

    }

}
