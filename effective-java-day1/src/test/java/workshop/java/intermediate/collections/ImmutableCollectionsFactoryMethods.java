package workshop.java.intermediate.collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class ImmutableCollectionsFactoryMethods {

    @Test
    public void listOfFactoryMethod() throws Exception {

        // interfaces of all collections in java allow to modify entries
        // we dont need to wrap it if we want prevent further modifications of that list
        List<String> unmodifiable = List.of("some", "some other");

        // now we can get entries or iterate
        assertThat(unmodifiable)
                .containsExactly("some", "some other");

        // but any modification will throw error UnsupportedOperationException
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> unmodifiable.add("another"));
    }

    @Test
    public void setOfFactoryMethod() throws Exception {

        // interfaces of all collections in java allow to modify entries
        // we dont need to wrap it if we want prevent further modifications of that list
        Set<String> unmodifiable = Set.of("some", "some other");

        // now we can get entries or iterate
        assertThat(unmodifiable)
                .containsExactly("some", "some other");

        // but any modification will throw error UnsupportedOperationException
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> unmodifiable.add("another"));
    }

    @Test
    public void mapOfFactoryMethod() throws Exception {
        // interfaces of all collections in java allow to modify entries
        // we dont need to wrap it if we want prevent further modifications of that list
        Map<String, Integer> unmodifiable = Map.of("some", 1, "some other", 2);

        // now we can get entries or iterate
        assertThat(unmodifiable)
                .containsKeys("some", "some other")
                .containsValues(1, 2);

        // but any modification will throw error UnsupportedOperationException
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> unmodifiable.put("another", 3));
    }

    @Test
    public void mapOfEntriesFactoryMethod() throws Exception {

        // interfaces of all collections in java allow to modify entries
        // we dont need to wrap it if we want prevent further modifications of that list
        Map<String, Integer> unmodifiable = Map.ofEntries(
                Map.entry("some", 1),
                Map.entry("some other", 2)
        );

        // now we can get entries or iterate
        assertThat(unmodifiable)
                .containsKeys("some", "some other")
                .containsValues(1, 2);

        // but any modification will throw error UnsupportedOperationException
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> unmodifiable.put("another", 3));
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Test
    public void nullAreUnWelcome() throws Exception {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> List.of(null, "some other"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Set.of(null, "some other"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() ->
                        Map.ofEntries(
                                Map.entry(null, 1),
                                Map.entry("some other", 2)
                        )
                );

    }

    @SuppressWarnings("deprecation")
    @Test
    public void notTrulyImmutableCollections() throws Exception {

        // collection will be truly immutable
        // if we cannot edit entries in collection
        // and each entry is immutable

        // two instances of lists but equal
        List<Date> dateOnList1 = List.of(new Date(2016 - 1900, 6 - 1, 15));
        List<Date> dateOnList2 = List.of(new Date(2016 - 1900, 6 - 1, 15));
        assertThat(dateOnList1).isEqualTo(dateOnList2);

        // old mutable java.util.Date representation
        Date date20160615 = dateOnList2.get(0);

        // change value of year
        date20160615.setYear(2020 - 1900);
        // same date instance but internal state is changed
        assertThat(date20160615).isSameAs(dateOnList2.get(0));

        // unfortunately lists are no longer equals
        // even if lists are unmodifiable
        assertThat(dateOnList1).isNotEqualTo(dateOnList2);

        System.out.println(dateOnList1);
        System.out.println(dateOnList2);
    }
}
