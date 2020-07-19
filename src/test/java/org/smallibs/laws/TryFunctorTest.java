package org.smallibs.laws;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.smallibs.data.Try;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.util.FunctionsHelper.compose;
import static org.smallibs.util.FunctionsHelper.id;

@RunWith(JUnitQuickcheck.class)
public class TryFunctorTest {

    @Property
    @Law("map id = id")
    public void identity(String s) {
        assertThat(
                Try.success(s).map(id())
        ).isEqualTo(
                id().apply(Try.success(s))
        );
    }

    @Property
    @Law("map (f . g) = map f . map g")
    public void composition(int s) {
        final Function<Integer, String> f = String::valueOf;
        final Function<Integer, Integer> g = v -> v + 1;

        assertThat(
                Try.success(s).map(compose(f, g))
        ).isEqualTo(
                compose(
                        (Try<Integer> x) -> x.map(f),
                        (Try<Integer> x) -> x.map(g)
                ).apply(Try.success(s))
        );
    }

}
