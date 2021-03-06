package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import de.synyx.meter.core.MeterProvider;
import de.synyx.meter.metrics.reporter.MetricReportHandler;
import de.synyx.meter.metrics.reporter.MetricReportMediator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

/**
 * Date: 06.08.2014
 * Time: 15:44
 */
@RunWith (MockitoJUnitRunner.class)
public class MetricReportTestSupport {

    @Mock
    protected MeterProvider provider;

    @Mock
    protected MetricRegistry registry;

    @Mock
    protected ScheduledReporter reporter;

    @Before
    public void before () {
        when (provider.unwrap (MetricRegistry.class)).thenReturn (registry);
    }

    @Test
    public void testMock () {
        assertThat (registry, notNullValue ());
        assertThat (reporter, notNullValue ());
    }

    final URI anyurl = anyuri (UUID.randomUUID ());

    final void assertReporter (ScheduledReporter reporter, MetricRegistry registry, String rate, String duration) throws NoSuchFieldException, IllegalAccessException {
        assertThat (field (ScheduledReporter.class, "registry",     MetricRegistry.class, reporter), equalTo (registry));
        assertThat (field (ScheduledReporter.class, "rateUnit",     String.class,         reporter), equalTo (rate));
        assertThat (field (ScheduledReporter.class, "durationUnit", String.class,         reporter), equalTo (duration));
    }

    final <T, S> T field (Class<? extends S> container, String name, Class<T> type, S parent) throws NoSuchFieldException, IllegalAccessException {
        Field field = container.getDeclaredField (name);
                field.setAccessible (true);

        return type.cast (field.get (parent));
    }

    final MetricReportMediator mediator () {
        return new MetricReportMediator (provider);
    }

    final MetricReportMediator mediator (MetricReportHandler handler) {
        return new MetricReportMediator (provider, handler);
    }

    final MetricReportHandler handler (final String scheme, final ScheduledReporter reporter) {
        class TestReportHandler extends MetricReportHandler {

            @Override
            protected String scheme () {
                return scheme;
            }

            @Override
            protected ScheduledReporter reporter (MetricReportMediator mediator, MeterProvider provider, URI location) {
                return reporter;
            }
        }

        return new TestReportHandler ();
    }

    final URI anyuri (UUID uuid) {
        /* prefix with a letter as uri schemes aren't allowed to start with a numerical value */
        return URI.create ("A" + anytext (uuid) + "://" + anytext (UUID.randomUUID ()));
    }

    final String anytext (UUID uuid) {
        return uuid.toString ().replace ("-", "");
    }

}
