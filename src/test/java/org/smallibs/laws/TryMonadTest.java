package org.smallibs.laws;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.smallibs.data.Try;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitQuickcheck.class)
public class TryMonadTest {

    @Property
    @Law("pure >>= f = f")
    public void identity1(int s) {
        final Function<Integer, Try<String>> f = x -> Try.success(String.valueOf(x));

        assertThat(
                Try.success(s).flatmap(f)
        ).isEqualTo(
                f.apply(s)
        );
    }

    @Property
    @Law("m >>= pure = pure")
    public void identity2(int s) {
        assertThat(
                Try.success(s).flatmap(Try::pure)
        ).isEqualTo(
                Try.pure(s)
        );
    }

    @Property
    @Law("(m >>= f) >>= g = (x -> f x >>= g)")
    public void associativity(int s) {
        final Function<Integer, Try<String>> f = i -> Try.success(String.valueOf(i));
        final Function<Integer, Try<Integer>> g = v -> Try.success(v + 1);

        assertThat(
                Try.success(s).flatmap(g).flatmap(f)
        ).isEqualTo(
                g.apply(s).flatmap(f)
        );
    }
}
