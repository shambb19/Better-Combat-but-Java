package util;

import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Filterable<T> {

    private final Stream<T> stream;

    public <E> Filterable<E> castTo(Class<E> type) {
        return new Filterable<>(
                stream.filter(type::isInstance)
                        .map(type::cast)
        );
    }

    public <E> List<E> castToAsList(Class<E> type) {
        return castTo(type).toList();
    }

    public T firstOrElseThrow(Predicate<T> condition) {
        return stream.filter(condition).findFirst().orElseThrow();
    }

    public Filterable<T> filteredBy(Predicate<T> condition) {
        return new Filterable<>(stream.filter(condition));
    }

    public List<T> filteredByAsList(Predicate<T> condition) {
        return filteredBy(condition).toList();
    }

    public T firstWithToStringEquals(String query) {
        return stream.filter(t -> String.valueOf(t).equalsIgnoreCase(query))
                .findFirst()
                .orElse(null);
    }

    public T firstOrNull() {
        return stream.findFirst().orElse(null);
    }

    public void forEach(Consumer<T> forEach) {
        stream.forEach(forEach);
    }

    public List<T> toList() {
        return stream.collect(Collectors.toList());
    }

    @SafeVarargs
    public static <S> Filterable<S> ofLists(List<S>... lists) {
        ArrayList<S> list = new ArrayList<>();
        for (List<S> l : lists) list.addAll(l);
        return of(list);
    }

    @SafeVarargs
    public static <S> Filterable<S> ofArrays(S[]... arrays) {
        ArrayList<S> list = new ArrayList<>();
        for (S[] arr : arrays) Collections.addAll(list, arr);
        return of(list);
    }

    public static <S> Filterable<S> of(Stream<S> stream) {
        return new Filterable<>(stream);
    }

    public static <S> Filterable<S> of(Iterable<S> iterable) {
        Stream<S> stream = StreamSupport.stream(iterable.spliterator(), false);
        return new Filterable<>(stream);
    }

    @SafeVarargs
    public static <S> Filterable<S> of(S... items) {
        return new Filterable<>(Stream.of(items));
    }
}