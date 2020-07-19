package org.smallibs.laws;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.smallibs.concurrent.promise.Promise;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitQuickcheck.class)
public class PromiseMonadTest {

    @Property
    @Law("pure >>= f = f")
    public void identity1(int s) throws Throwable {
        final Function<Integer, Promise<String>> f = x -> Promise.pure(String.valueOf(x));

        assertThat(
                Promise.pure(s).flatmap(f).getFuture().get()
        ).isEqualTo(
                f.apply(s).getFuture().get()
        );
    }

    @Property
    @Law("m >>= pure = pure")
    public void identity2(int s) throws Throwable {
        assertThat(
                Promise.pure(s).flatmap(Promise::pure).getFuture().get()
        ).isEqualTo(
                Promise.pure(s).getFuture().get()
        );
    }

    @Property
    @Law("(m >>= f) >>= g = (x -> f x >>= g)")
    public void associativity(int s) throws Throwable {
        final Function<Integer, Promise<String>> f = i -> Promise.pure(String.valueOf(i));
        final Function<Integer, Promise<Integer>> g = v -> Promise.pure(v + 1);

        assertThat(
                Promise.pure(s).flatmap(g).flatmap(f).getFuture().get()
        ).isEqualTo(
                g.apply(s).flatmap(f).getFuture().get()
        );
    }
}
