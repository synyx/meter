package de.synyx.metrics.core.internal;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import de.synyx.metrics.core.Meter;
import de.synyx.metrics.core.MeterProvider;

import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.measure.Measurable;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * Date: 12.08.2014
 * Time: 14:47
 */
public final class DefaultMetricMeterProvider implements MeterProvider {

    private final MetricRegistry registry;

    @Inject
    public DefaultMetricMeterProvider (MetricRegistry registry) {
        this.registry = registry;
    }

    @Override
    public final Meter<Dimensionless> counter (final String name) {
        return new Meter<Dimensionless> () {

            private final Counter counter = registry.counter (name);

            @Override
            public final Meter<Dimensionless> update (Measurable<Dimensionless> point) {
                       this.counter.inc (point.longValue (Unit.ONE));
                return this;
            }

        };
    }

    @Override
    public final Meter<Dimensionless> histogram (final String name) {
        return new Meter<Dimensionless> () {

            private final Histogram histogram = registry.histogram (name);

            @Override
            public final Meter<Dimensionless> update (Measurable<Dimensionless> point) {
                       this.histogram.update (point.longValue (Unit.ONE));
                return this;
            }

        };
    }

    @Override
    public final Meter<Dimensionless> meter (final String name) {
        return new Meter<Dimensionless> () {

            private final com.codahale.metrics.Meter meter = registry.meter (name);

            @Override
            public final Meter<Dimensionless> update (Measurable<Dimensionless> point) {
                       this.meter.mark (point.longValue (Unit.ONE));
                return this;
            }

        };
    }

    @Override
    public final Meter<Duration> timer (final String name) {
        return new Meter<Duration> () {

            private final Timer timer = registry.timer (name);

            @Override
            public final Meter<Duration> update (Measurable<Duration> point) {
                       this.timer.update (point.longValue (SI.NANO (SI.SECOND)), TimeUnit.NANOSECONDS);
                return this;
            }

        };
    }

}