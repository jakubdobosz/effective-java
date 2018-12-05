package workshop.java.intermediate.fancystuff;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import workshop.java.intermediate.boilerplatefree.ExampleMovies;
import workshop.java.intermediate.boilerplatefree.Movie;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static workshop.java.intermediate.almostlikefunctional.$ExitTest.Try.tryIt;

/**
 *
 */
@Disabled
public class JavaScriptTest {

    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    private void loadClientLambdas(String js) throws Exception {
        engine.eval(js);
    }

    private Predicate<Movie> predicate(String predicate) throws Exception {
        Invocable invocable = (Invocable) engine;

        return movie -> tryIt(() ->
                (boolean) invocable.invokeFunction(predicate, movie)
        ).getUnchecked();
    }

    @Test
    public void configurableJSLambds() throws Exception {
        String js = "" +
                "var filter = function(movie) {\n" +
                "    return movie.imdbRating > 8.0;\n" +
                "};" +
                "";
        loadClientLambdas(js);

        Predicate<Movie> filter = predicate("filter");

        List<Movie> filtered = ExampleMovies.allMovies().stream()
                .map(Movie.MovieBuilder::build)
                .filter(filter)
                .collect(Collectors.toList());

        assertThat(filtered)
                .extracting(Movie::getTitle)
                .doesNotContain("The Inglorious Bastards")
                .contains("The Wizard of Oz")
                .hasSize(2)
        ;
    }
}
