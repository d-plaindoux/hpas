package org.smallibs.laws;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.smallibs.data.Maybe;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitQuickcheck.class)
public class MaybeMonadTest {

    @Property
    @Law("pure >>= f = f")
    public void identity1(int s) {
        final Function<Integer, Maybe<String>> f = x -> Maybe.some(String.valueOf(x));

        assertThat(
                Maybe.some(s).flatmap(f)
        ).isEqualTo(
                f.apply(s)
        );
    }

    @Property
    @Law("m >>= pure = pure")
    public void identity2(int s) {
        assertThat(
                Maybe.some(s).flatmap(Maybe::pure)
        ).isEqualTo(
                Maybe.pure(s)
        );
    }

    @Property
    @Law("(m >>= f) >>= g = (x -> f x >>= g)")
    public void associativity(int s) {
        final Function<Integer, Maybe<String>> f = i -> Maybe.some(String.valueOf(i));
        final Function<Integer, Maybe<Integer>> g = v -> Maybe.some(v + 1);

        assertThat(
                Maybe.some(s).flatmap(g).flatmap(f)
        ).isEqualTo(
                g.apply(s).flatmap(f)
        );
    }
}
