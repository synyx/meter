package de.synyx.metrics.internal;

import com.codahale.metrics.MetricRegistry;
import de.synyx.metrics.MetricHook;
import de.synyx.metrics.MetricInvocation;
import de.synyx.metrics.MetricNaming;
import de.synyx.metrics.annotation.Counter;
import de.synyx.metrics.annotation.Histogram;
import de.synyx.metrics.annotation.Meter;
import de.synyx.metrics.annotation.Metric;
import de.synyx.metrics.annotation.Timer;
import de.synyx.metrics.hook.MetricHookCounter;
import de.synyx.metrics.hook.MetricHookHistogram;
import de.synyx.metrics.hook.MetricHookMeter;
import de.synyx.metrics.hook.MetricHookTimer;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultMetricMethodInterceptorTest {

    private final MetricNaming naming = mock (MetricNaming.class);

    private final MetricRegistry registry = new MetricRegistry ();

    private final MetricInvocation invoker = mock (MetricInvocation.class);

    private final DefaultMetricMethodInterceptor interceptor = new DefaultMetricMethodInterceptor (mock (ServiceLocator.class), registry, naming, invoker);

    {
        when (naming.name (anyString ())).thenAnswer (new Answer<Object> () {

            @Override
            public Object answer (InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments ()[0];
            }

        });

        try {
            //noinspection unchecked
            when (invoker.invoke (any (Callable.class), any (List.class))).thenAnswer (new Answer<Object> () {

                @SuppressWarnings ("unchecked")
                @Override
                public Object answer (InvocationOnMock invocation) throws Throwable {
                    /* test do not care about the hook execution -> tested by separate units */
                    return ((Callable<Object>) invocation.getArguments ()[0]).call ();
                }

            });
        } catch (Throwable throwable) {
            throw new RuntimeException (throwable.getCause ());
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testInvokeAll () throws Throwable {
        List<MetricHook> hooks = invoke (all ());

        assertThat (hooks.size (), equalTo (4));
        assertThat (hooks.get (0), instanceOf (MetricHookCounter.class));
        assertThat (hooks.get (1), instanceOf (MetricHookHistogram.class));
        assertThat (hooks.get (2), instanceOf (MetricHookMeter.class));
        assertThat (hooks.get (3), instanceOf (MetricHookTimer.class));
    }

    @Test
    public void testInvokeOne () throws Throwable {
        List<MetricHook> hooks = invoke (one ());

        assertThat (hooks.size (), equalTo (1));
        assertThat (hooks.get (0), instanceOf (MetricHookCounter.class));
    }

    @Test (expected = IllegalStateException.class)
    public void testInvokeNoAnnotation () throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (nothing ());

        interceptor.invoke (invocation);
    }

    @Test (expected = RuntimeException.class)
    public void testInvokeFail () throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (all ());
        when (invocation.proceed ()).thenThrow (new RuntimeException ("fail"));

        interceptor.invoke (invocation);
    }

    @Test (expected = Exception.class)
    public void testInvokeFailHarder () throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (all ());
        when (invocation.proceed ()).thenThrow (new Throwable ("fail"));

        interceptor.invoke (invocation);
    }

    @SuppressWarnings ("unchecked")
    private List<MetricHook> invoke (Method method) throws Throwable {
        MethodInvocation invocation;

              invocation = mock (MethodInvocation.class);
        when (invocation.getMethod ()).thenReturn (method);

        interceptor.invoke (invocation);

        ArgumentCaptor<List<MetricHook>> captor = ArgumentCaptor.forClass ((Class) List.class);

        verify (invoker).invoke (Mockito.any (Callable.class), captor.capture ());

        return captor.getValue ();
    }

    @Test
    public void testCounter () throws Exception {
        String name = UUID.randomUUID ().toString ();

        Counter counter;

              counter = mock (Counter.class);
        when (counter.value ()).thenReturn (name);

        assertThat (interceptor.counter (nothing ()).apply (counter), instanceOf (MetricHookCounter.class));

        assertThat (registry.counter (name), notNullValue ());
    }

    @Test
    public void testHistogram () throws Exception {
        String name = UUID.randomUUID ().toString ();

        Histogram histogram;

              histogram = mock (Histogram.class);
        when (histogram.value ()).thenReturn (name);

        assertThat (interceptor.histogram (nothing ()).apply (histogram), instanceOf (MetricHookHistogram.class));

        assertThat (registry.histogram (name), notNullValue ());
    }

    @Test
    public void testMeter () throws Exception {
        String name = UUID.randomUUID ().toString ();

        Meter meter;

              meter = mock (Meter.class);
        when (meter.value ()).thenReturn (name);

        assertThat (interceptor.meter (nothing ()).apply (meter), instanceOf (MetricHookMeter.class));

        assertThat (registry.meter (name), notNullValue ());
    }

    @Test
    public void testTimer () throws Exception {
        String name = UUID.randomUUID ().toString ();

        Timer timer;

              timer = mock (Timer.class);
        when (timer.value ()).thenReturn (name);

        assertThat (interceptor.timer (nothing ()).apply (timer), instanceOf (MetricHookTimer.class));

        assertThat (registry.timer (name), notNullValue ());
    }

    @Test
    public void testName () throws Exception {
        String val = UUID.randomUUID ().toString ();

        assertThat (interceptor.name (nothing (),       val).get (), equalTo (                                                                                   val));
        assertThat (interceptor.name (nothing (), "#" + val).get (), equalTo ("de.synyx.metrics.internal.DefaultMetricMethodInterceptorTest$TestType.nothing." + val));
    }

    private Method nothing () throws NoSuchMethodException {
        return TestType.class.getMethod ("nothing");
    }

    private Method all () throws NoSuchMethodException {
        return TestType.class.getMethod ("all");
    }

    private Method one () throws NoSuchMethodException {
        return TestType.class.getMethod ("one");
    }

    public final static class TestType {

        public void nothing () {}

        @Metric (
            counters   = @Counter   ("counter"),
            histograms = @Histogram ("histogram"),
            meters     = @Meter     ("meter"),
            timers     = @Timer     ("timer")
        )
        public void all () {}

        @Metric (
            counters   = @Counter   ("counter")
        )
        public void one () {}

    }

}