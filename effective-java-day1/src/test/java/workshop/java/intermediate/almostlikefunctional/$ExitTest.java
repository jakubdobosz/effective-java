package workshop.java.intermediate.almostlikefunctional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Disabled
public class $ExitTest {

    // Task 1.
    // write Try monad
    // with static factory methods taking functional interface ThrowingSupplier
    // implement isSuccess method
    // implement getNullable, getOptional, getThrowing, getUnchecked
    // implement map, flatMap methods
    // implement orElse, orElseGet
    // implement recover(Function<? super Throwable, T> f)
    // provide public static factory methods successful and failure
    // does it make sens to accept ThrowingRunnable in static factory method ?

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }

    public interface Try<T> {

        static <Input> Supplier<Try<Input>> supplier(ThrowingSupplier<Input> supplier) {
            return () -> tryIt(supplier);
        }

        static <Input> Try<Input> tryIt(ThrowingSupplier<Input> supplier) {
            // TODO try implement as Sealed classes if you can
            return null;
        }

        boolean isSuccess();

        T getNullable();

        Optional<T> getOptional();

        T getOrThrow() throws Throwable;

        T getOrThrowUnchecked();

        T orElse(T def);

        T orElseGe(Supplier<T> defSupplier);

        <U> Try<U> map(Function<T, U> f);

        //<U> Try<U> flatMap(Function<T, Try<U>> f);
    }

    @Test
    public void first() throws Exception {
        boolean isSuccess = Try.tryIt(() -> "It works :)").isSuccess();

        Assertions.assertThat(isSuccess).isTrue();
    }

    @Test
    public void getOptional() throws Exception {
        String nonEmpty = Try.tryIt(() -> "it worked")
                .getOptional()
                .filter(s -> !s.isEmpty())
                .get();

        String empty = Try.<String>tryIt(() -> {
            throw new RuntimeException("thrown from monad");
        }).getOptional().orElse("");

        Assertions.assertThat(nonEmpty).isNotEmpty();
        Assertions.assertThat(empty).isEmpty();
    }

    @Test
    public void getUnchecked() throws Exception {
        Try<Object> aTry = Try.tryIt(() -> {
            throw new RuntimeException("thrown from monad");
        });

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(
                        aTry::getOrThrowUnchecked
                )
        ;
    }

    @Test
    public void map() throws Exception {
        byte[] nonEmpty = Try.tryIt(() -> "it worked")
                .map(String::toUpperCase)
                .map(String::getBytes)
                .getOptional()
                .filter((s) -> s.length > 0)
                .get();

        System.out.println(Arrays.toString(nonEmpty));
    }

    @Test
    public void tryItOnCheckedExceptionStatement() throws Exception {
        Optional<String> s1 = Optional.of("slow processed string")
                .flatMap(s -> Try.tryIt(() -> {
                            Thread.sleep(100);
                            return s;
                        }).getOptional()
                );

        Assertions.assertThat(s1).hasValue("slow processed string");
    }

    // Task 2.
    // write ObservableValue class
    // implement set and get methods
    // implement map, filter, consume methods
    // implement map, filter, consume variant-'Bi' taking old and new values
    // implement combine(BiFunction<T, ObservableValue<U>, R> f)

}
