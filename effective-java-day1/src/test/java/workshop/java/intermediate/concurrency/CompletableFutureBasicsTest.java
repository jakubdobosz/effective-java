package workshop.java.intermediate.concurrency;

import org.junit.jupiter.api.Test;
import workshop.java.intermediate.boilerplatefree.ExampleMovies;
import workshop.java.intermediate.boilerplatefree.Movie;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 *
 */
public class CompletableFutureBasicsTest {

    Executor IO = Executors.newCachedThreadPool();

    @Test
    void playWithCompletableFuture() {
        List<Movie.MovieBuilder> database = ExampleMovies.allMovies();

        CompletableFuture.supplyAsync(() -> database.stream().count(), IO)
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> fetchData(database), IO)
                                .thenApplyAsync(
                                        this::createTransportObject,
                                        ForkJoinPool.commonPool()),
                        this::createPage
                ).join();
    }

    private Object createPage(Long count, List<Movie> data) {
        return null;
    }

    private List<Movie.MovieBuilder> fetchData(List<Movie.MovieBuilder> database) {
        return database.stream().collect(Collectors.toList());
    }

    private List<Movie> createTransportObject(List<Movie.MovieBuilder> movieBuilders) {
        return movieBuilders.stream().map(Movie.MovieBuilder::build).collect(Collectors.toList());
    }

    @Test
    public void runTask() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> sleep(Duration.ofSeconds(1)));

        assertThat(future).isNotDone();
        Void aVoid = future.get(); // waits till complete
        assertThat(aVoid).isNull();
        assertThat(future).isDone();
    }

    @Test
    public void runDataSupplier() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Future is now !";
        });

        assertThat(future).isNotDone();
        String providedValue = future.get(); // waits till complete
        assertThat(providedValue).isEqualTo("Future is now !");
        assertThat(future).isDone();
    }

    @Test
    public void createJustValueHolder() throws Exception {
        CompletableFuture<String> future = CompletableFuture.completedFuture("Future is now !");

        assertThat(future).isDone();
        String providedValue = future.get(); // already completed
        assertThat(providedValue).isEqualTo("Future is now !");
        assertThat(future).isDone();
    }

    @Test
    public void applyFunction() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Future is now !";
        }).thenApply(String::toUpperCase);

        assertThat(future).isNotDone();
        String providedValue = future.get(); // waits till complete
        assertThat(providedValue).isEqualTo("FUTURE IS NOW !");
        assertThat(future).isDone();
    }

    @Test
    public void applyFunctionToDifferentType() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Future is now !";
        }).thenApply(String::length);

        assertThat(future).isNotDone();
        int providedValue = future.get(); // waits till complete
        assertThat(providedValue).isEqualTo(15);
        assertThat(future).isDone();
    }

    @Test
    public void consumeValue() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Future is now !";
        }).thenAccept(System.out::println);

        assertThat(future).isNotDone();

        await("text printed to console")
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(future).isDone();
                });
    }

    @Test
    public void runNextTask() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Future is now !";
        }).thenRun(() -> System.out.println("Done!"));

        assertThat(future).isNotDone();
        future.get(); // waits till complete
        assertThat(future).isDone();
    }

    @Test
    public void createFutureNewWhenFirstIsDone() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "someID from database";
        });

        CompletableFuture<String> future2 = future1.thenCompose(this::requestDataFromCloudForId);

        assertThat(future1).isNotDone();
        assertThat(future2).isNotDone();
        String request = future1.get(); // waits till complete
        assertThat(request).isEqualTo("someID from database");
        assertThat(future1).isDone();
        assertThat(future2).isNotDone();
        String response = future2.get(); // waits till complete
        assertThat(response).isEqualTo("data for someID from database");
        assertThat(future2).isDone();
    }

    @Test
    public void joinTwoAndCombineValues() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(2));
            return "Future is now !";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Be prepared for it !";
        });

        CompletableFuture<String> joined = future1.thenCombine(future2, (string1, string2) ->
                string1.replaceAll(" !$", ",") + " so " + string2.toLowerCase()
        );

        assertThat(joined).isNotDone();
        String value = joined.get(); // waits till complete
        assertThat(value).isEqualTo("Future is now, so be prepared for it !");
        assertThat(future1).isDone();
        assertThat(future2).isDone();
        assertThat(joined).isDone();
    }

    @Test
    public void joinTwoAndConsume() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Future is now !";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Be prepared for it !";
        });

        CompletableFuture<Void> joined = future1.thenAcceptBoth(future2, (string1, string2) ->
                System.out.println(string1 + " " + string2)
        );

        assertThat(joined).isNotDone();
        joined.get(); // waits till complete
        assertThat(future1).isDone();
        assertThat(future2).isDone();
        assertThat(joined).isDone();
    }

    @Test
    public void joinTwoAndJustRunATask() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Future is now !";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Be prepared for it !";
        });

        CompletableFuture<Void> joined = future1.runAfterBoth(future2, () ->
                System.out.println("Done!")
        );

        assertThat(joined).isNotDone();
        joined.get(); // waits till complete
        assertThat(future1).isDone();
        assertThat(future2).isDone();
        assertThat(joined).isDone();
    }

    @Test
    public void allDone() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(2));
            return "Future is now !";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Be prepared for it !";
        });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);

        assertThat(future1).isNotDone();
        assertThat(future2).isNotDone();
        assertThat(allOf).isNotDone();
        allOf.get();
        assertThat(future1).isDone();
        assertThat(future2).isDone();
    }

    @Test
    public void consumeWhenFirstIsDone() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(2));
            return "Future is now !";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Be prepared for it !";
        });

        CompletableFuture<Void> firstDone = future1.acceptEither(future2, System.out::println);

        assertThat(future1).isNotDone();
        assertThat(future2).isNotDone();
        assertThat(firstDone).isNotDone();
        firstDone.get();
        assertThat(future1.isDone() && future2.isDone()).isFalse();
        assertThat(future1.isDone() || future2.isDone()).isTrue();
    }

    @Test
    public void applyFunctionToFirstDone() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(2));
            return "Future is now !";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Be prepared for it !";
        });

        CompletableFuture<String> firstDone = future1.applyToEither(future2, String::toUpperCase);

        assertThat(future1).isNotDone();
        assertThat(future2).isNotDone();
        assertThat(firstDone).isNotDone();
        firstDone.get();
        assertThat(future1.isDone() && future2.isDone()).isFalse();
        assertThat(future1.isDone() || future2.isDone()).isTrue();
    }

    @Test
    public void runTaskWhenFirstDone() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(2));
            return "Future is now !";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Be prepared for it !";
        });

        CompletableFuture<Void> firstDone = future1.runAfterEither(future2, () ->
                System.out.println("some is done")
        );

        assertThat(future1).isNotDone();
        assertThat(future2).isNotDone();
        assertThat(firstDone).isNotDone();
        firstDone.get();
        assertThat(future1.isDone() && future2.isDone()).isFalse();
        assertThat(future1.isDone() || future2.isDone()).isTrue();
    }

    @Test
    public void anyDone() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(2));
            return "Future is now !";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "Be prepared for it !";
        });

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future1, future2);

        assertThat(future1).isNotDone();
        assertThat(future2).isNotDone();
        assertThat(anyOf).isNotDone();
        anyOf.get();
        assertThat(future1.isDone() && future2.isDone()).isFalse();
        assertThat(future1.isDone() || future2.isDone()).isTrue();
    }

    private CompletionStage<String> requestDataFromCloudForId(String dataId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(Duration.ofSeconds(1));
            return "data for " + dataId;
        });
    }

    private void sleep(Duration seconds) {
        try {
            Thread.sleep(seconds.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
