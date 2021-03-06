package de.synyx.meter.metrics.reporter;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import de.synyx.meter.core.MeterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.net.URI;
import java.util.Map;

/**
 * Date: 06.08.2014
 * Time: 11:39
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public final class MetricReportHandlerLog extends MetricReportHandler {

    /** {@inheritDoc} */
    @Override
    protected final String scheme () {
        return "log";
    }

    /** {@inheritDoc} */
    @Override
    protected final ScheduledReporter reporter (MetricReportMediator mediator, MeterProvider provider, URI location) {
        Map<String, String> parameters = parameters (location);

        Logger logger = logger (location);

        Slf4jReporter reporter = Slf4jReporter.forRegistry (provider.unwrap (MetricRegistry.class))
                                          .outputTo           (logger)
                                          .markWith           (marker       (parameters.get ("marker")        ))
                                          .convertRatesTo     (timeunit (or (parameters.get ("rate"),     "ms")))
                                          .convertDurationsTo (timeunit (or (parameters.get ("duration"),  "s")))
                                              .filter (MetricFilter.ALL)
                                                  .build ();

        return start (reporter, parameters.get ("refresh"));
    }

    private Logger logger (URI location) {
        String authority;

                                        authority = location.getAuthority ();
        return LoggerFactory.getLogger (authority == null ? "com.codahale.metrics.application.logger" : authority);
    }

    private Marker marker (String name) {
        return name == null ? null : MarkerFactory.getMarker (name);
    }

}
