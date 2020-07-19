package org.smallibs.laws;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.smallibs.data.Maybe;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.util.FunctionsHelper.compose;
import static org.smallibs.util.FunctionsHelper.id;

@RunWith(JUnitQuickcheck.class)
public class MaybeFunctorTest {

    @Property
    @Law("map id = id")
    public void identity(String s) {
        assertThat(
                Maybe.some(s).map(id())
        ).isEqualTo(
                id().apply(Maybe.some(s))
        );
    }

    @Property
    @Law("map (f . g) = map f . map g")
    public void composition(int s) {
        final Function<Integer, String> f = String::valueOf;
        final Function<Integer, Integer> g = v -> v + 1;

        assertThat(
                Maybe.some(s).map(compose(f, g))
        ).isEqualTo(
                compose(
                        (Maybe<Integer> x) -> x.map(f),
                        (Maybe<Integer> x) -> x.map(g)
                ).apply(Maybe.some(s))
        );
    }

}
