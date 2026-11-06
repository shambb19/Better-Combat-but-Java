package util.function;

@FunctionalInterface
public interface Writer<T> {
    String write(T from);
}
