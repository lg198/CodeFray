package com.github.lg198.codefray.util;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SingleCollector<T> implements Collector<T, SingleContainer<T>, T> {

    @Override
    public Supplier<SingleContainer<T>> supplier() {
        return () -> new SingleContainer<>();
    }

    @Override
    public BiConsumer<SingleContainer<T>, T> accumulator() {
        return (cont, t) -> cont.single = t;
    }

    @Override
    public BinaryOperator<SingleContainer<T>> combiner() {
        return (cont1, cont2) -> cont1.single == null ? cont2 : cont1;
    }

    @Override
    public Function<SingleContainer<T>, T> finisher() {
        return (cont) -> cont.single;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.UNORDERED);
    }
}
