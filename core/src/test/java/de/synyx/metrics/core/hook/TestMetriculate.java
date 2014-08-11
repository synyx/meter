package de.synyx.metrics.core.hook;

import de.synyx.metrics.core.Metriculate;

/**
* Date: 30.07.2014
* Time: 13:46
*/
public class TestMetriculate implements Metriculate {

    private final long value;

    public TestMetriculate (long value) {
        this.value = value;
    }

    @Override
    public long determine (Object response, Throwable throwable) {
        return value;
    }

}