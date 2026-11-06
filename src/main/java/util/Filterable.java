package util;

import lombok.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Filterable<T> {

    public final Stream<T> stream;

    /**
     * Filters the filterable object by the given predicate
     * @param condition the condition by which the filterable is to be filtered
     * @return the new filterable
     */
    public Filterable<T> filteredBy(Predicate<T> condition) {
        return new Filterable<>(stream.filter(condition));
    }

    /**
     * Filters the filterable object such that the output of the classifier equals the comparator
     * @param classifier the function whose value is compared to the comparator
     * @param comparator the object to be compared to the classifier's output
     * @return the new filterable
     * @param <U> the type of the classifier's output
     */
    public <U> Filterable<T> filteredBy(Function<T, U> classifier, U comparator) {
        return filteredBy(t -> classifier.apply(t).equals(comparator));
    }

    /**
     * Maps the filterable to the result of the given mapper
     * @param mapper the function whose result will be mapped in the new filterable
     * @return the new filterable
     * @param <U> the type of the new mapped filterable
     */
    public <U> Filterable<U> mapped(Function<T, U> mapper) {
        return new Filterable<>(stream.map(mapper));
    }

    /**
     * Returns an array generated from the filterable object
     * @param generator the required array generator
     * @return the new array
     * @param <A> the type of the new array (same as T)
     */
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return stream.toArray(generator);
    }

    /**
     * Returns a list generated from the filterable object
     * @return the new list
     */
    public List<T> toList() {
        return stream.collect(Collectors.toList());
    }

    /**
     * Returns a set generated from the filterable object
     * @return the new set
     */
    public Set<T> toSet() {
        return stream.collect(Collectors.toSet());
    }

    /**
     * Returns a filterable object of the given collection
     * @param iterable the collection to be streamed
     * @return the new filterable object
     * @param <S> the type of the new filterable
     */
    public static <S> Filterable<S> of(Collection<S> iterable) {
        return new Filterable<>(iterable.stream());
    }

    /**
     * Returns a filterable object of the given collection that has been
     * mapped to the given class
     * @param iterable the collection to be streamed
     * @param classifier the class to which the collection is to be mapped
     * @return the new filterable object
     * @param <R> the type of the collection
     * @param <S> the type of the new mapped filterable object
     */
    public static <R, S extends R> Filterable<S> of(Collection<R> iterable, Class<S> classifier) {
        Stream<S> stream = iterable.stream()
                .filter(classifier::isInstance)
                .map(classifier::cast);
        return new Filterable<>(stream);
    }

    /**
     * Returns a filterable object of the given array that has been
     * mapped to the given class
     * @param arr the array to be streamed
     * @param classifier the class to which the array is to be mapped
     * @return the new filterable object
     * @param <R> the type of the array
     * @param <S> the type of the new mapped filterable object
     */
    public static <R, S extends R> Filterable<S> of(R[] arr, Class<S> classifier) {
        return of(Arrays.asList(arr), classifier);
    }

    /**
     * Returns a filterable object conjoining the given varargs collections
     * @param collections the collections to be streamed
     * @return the new filterable object
     * @param <S> the type of the new filterable object
     */
    @SafeVarargs
    public static <S> Filterable<S> ofCollections(Collection<S>... collections) {
        ArrayList<S> list = new ArrayList<>();
        for (Collection<S> c : collections) list.addAll(c);
        return of(list);
    }

}