package io.github.itzispyder.improperui.util.misc;

import java.util.function.Consumer;
import java.util.function.Function;

public class Voidable<T> {

    private final T value;

    private Voidable(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public <U> Voidable<U> map(Function<T, U> function) {
        return isPresent() ? of(function.apply(value)) : of(null);
    }

    public void accept(Consumer<T> action) {
        if (isPresent()) {
            action.accept(value);
        }
    }

    public void accept(Consumer<T> action, Runnable orElse) {
        if (isPresent()) {
            action.accept(value);
        }
        else {
            orElse.run();
        }
    }

    public T getOrDef(T fallback) {
        return isPresent() ? value : fallback;
    }

    public T getOrThrow(String msg, Object... args) {
        if (isPresent())
            return value;
        throw new IllegalArgumentException(msg.formatted(args));
    }

    public T getOrThrow() {
        return getOrThrow("value is not present.");
    }

    public static <T> Voidable<T> of(T value) {
        return new Voidable<>(value);
    }
}