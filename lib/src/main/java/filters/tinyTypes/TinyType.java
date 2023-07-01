package filters.tinyTypes;

public interface TinyType<T> extends Comparable<TinyType<T>> {

    T get();
}
