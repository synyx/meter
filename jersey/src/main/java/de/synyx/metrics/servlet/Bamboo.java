package de.synyx.metrics.servlet;

import de.synyx.metrics.service.BambooService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Date: 15.07.2014
 * Time: 15:51
 */
@Path ("bamboo")
public class Bamboo {

    @Inject
    private BambooService<String> service;

    @GET
    //    @Metric (
    //            timers     = @Timer,
    //            histograms = @Histogram (value = "#size", metriculate = BambooHistogramHook.class)
    //    )
    @Path ("{larry}")
    public final String echo (@QueryParam ("foo") String foo, @PathParam ("larry") String larry) {
        return service.call (foo + "#" + larry);
    }

}