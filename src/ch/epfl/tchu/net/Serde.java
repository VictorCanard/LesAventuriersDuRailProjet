package ch.epfl.tchu.net;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Serde<T> {
    String serialize(T objectToSerialize);

    Object deserialize(String stringToDeserialize);

    default Serde<T> of(Function<T, String> serializingFunction, Function<String, T> deserializingFunction){

        return new Serde<>() {
            @Override
            public String serialize(T objectToSerialize) {
                return serializingFunction.apply(objectToSerialize);
            }

            @Override
            public Object deserialize(String stringToDeserialize) {
                return deserializingFunction.apply(stringToDeserialize);
            }
        };
    }
    default Serde<T> oneOf(List<T> listOfValuesOfEnumType){
        Function<T, String> serializingFunction = (t) -> String.valueOf(listOfValuesOfEnumType.indexOf(t));

        Function<String, T> deserializingFunction = (string) -> listOfValuesOfEnumType.get(Integer.parseInt(string));

        return of(serializingFunction, deserializingFunction);


    }
    default Serde<T> listOf(Serde<T> usedSerde, String delimiter){
        Function<T, String> serializingFunction = (t) -> new StringJoiner(delimiter)
                .add(usedSerde.serialize(t))
                .toString();

        Function<String, T> deserializingFunction = (string) -> Arrays.stream(string.split(Pattern.quote(delimiter), -1))
                .collect(Collectors.toList());

        return of(serializingFunction, deserializingFunction);


    }
    default <T>Serde bagOf(Serde<T> usedSerde, String delimiter){
        return new Serde<T>() {
            @Override
            public String serialize(T objectToSerialize) {

                return new StringJoiner(delimiter).add(usedSerde.serialize(objectToSerialize)).toString();
            }

            @Override
            public Object deserialize(String stringToDeserialize) {
                return stringToDeserialize.split(Pattern.quote(delimiter), -1);
            }
        };

    }

}
