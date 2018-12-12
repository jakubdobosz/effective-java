package workshop.java.intermediate.collectionsprocessing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import workshop.java.intermediate.boilerplatefree.ExampleMovies;
import workshop.java.intermediate.boilerplatefree.Movie;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class ImmutableCollectorsTest {

    @Test
    void collectingAndThenExample() {
        Map<String, Movie> moviesById = ExampleMovies.allMovies()
                .stream()
                .map(Movie.MovieBuilder::build)
                .collect(collectingAndThen(
                        toMap(Movie::getImdbID, Function.identity()),
                        Collections::unmodifiableMap));

        Assertions.assertThrows(
                UnsupportedOperationException.class,
                moviesById::clear
        );
    }

    @Test
    void toUnmodifiableMapExample() {
        Map<String, Movie> moviesById = ExampleMovies.allMovies()
                .stream()
                .map(Movie.MovieBuilder::build)
                .collect(toUnmodifiableMap(
                        Movie::getImdbID, Function.identity()));

        Assertions.assertThrows(
                UnsupportedOperationException.class,
                moviesById::clear
        );
    }
}
