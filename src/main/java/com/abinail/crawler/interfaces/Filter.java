package com.abinail.crawler.interfaces;

import java.util.Objects;

public interface Filter<T> {

    boolean test(T t);

    default Filter<T> negate() {
        return (t) -> !test(t);
    }

    default Filter<T> and(Filter<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    default Filter<T> or(Filter<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

}
