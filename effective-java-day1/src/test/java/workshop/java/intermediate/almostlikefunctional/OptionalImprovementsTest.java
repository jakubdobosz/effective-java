package workshop.java.intermediate.almostlikefunctional;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class OptionalImprovementsTest {

    @Test
    public void or() throws Exception {
        Optional<String> o = Optional.empty();

        Optional<String> hi = o.or(() -> Optional.of("Hi"));

        assertThat(hi).hasValue("Hi");
    }

    @Test
    public void ifPresentOrElse() throws Exception {
        Optional<Object> o = Optional.of("ok");
        o.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("empty")
        );
    }

    @Test
    public void stream() throws Exception {
        Optional.of("val1")
                .stream()
                .findFirst()
                .orElse("noval");

        Stream.of(
                Optional.empty(),
                Optional.of("val1"),
                Optional.empty(),
                Optional.of("val2"),
                Optional.empty(),
                Optional.of("val3"),
                Optional.empty())
                //instead of filter + map, just:
                .flatMap(Optional::stream)
                .forEach(System.out::println);
    }
}
