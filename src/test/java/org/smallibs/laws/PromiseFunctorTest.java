package org.smallibs.laws;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.smallibs.concurrent.promise.Promise;
import org.smallibs.util.FunctionsHelper;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smallibs.util.FunctionsHelper.compose;
import static org.smallibs.util.FunctionsHelper.fromFunction;
import static org.smallibs.util.FunctionsHelper.id;

@RunWith(JUnitQuickcheck.class)
public class PromiseFunctorTest {

    @Property
    @Law("map id = id")
    public void identity(String s) throws Throwable {
        assertThat(
                Promise.pure(s).map(fromFunction(id())).getFuture().get()
        ).isEqualTo(
                id().apply(Promise.pure(s).getFuture().get())
        );
    }

    @Property
    @Law("map (f . g) = map f . map g")
    public void composition(int s) throws Throwable {
        final Function<Integer, String> f = String::valueOf;
        final Function<Integer, Integer> g = v -> v + 1;

        assertThat(
                Promise.pure(s).map(fromFunction(compose(f, g))).getFuture().get()
        ).isEqualTo(
                compose(
                        (Promise<Integer> x) -> x.map(fromFunction(f)),
                        (Promise<Integer> x) -> x.map(fromFunction(g))
                ).apply(Promise.pure(s)).getFuture().get()
        );
    }

}
