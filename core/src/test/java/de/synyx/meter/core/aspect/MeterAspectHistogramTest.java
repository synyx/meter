package de.synyx.meter.core.aspect;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.synyx.meter.core.Measure;
import de.synyx.meter.core.annotation.Histogram;
import de.synyx.meter.core.Meter;
import de.synyx.meter.core.aop.Aspect;
import de.synyx.meter.core.annotation.Kind;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.measure.Measurable;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class MeterAspectHistogramTest extends MeterAspectTest {

    @Mock
    private Meter<Dimensionless> meter;

    private Supplier<Meter<Dimensionless>> supplier;

    @Before
    public void before () {
        supplier = Suppliers.ofInstance (meter);
    }

    @Test
    public void testName () throws NoSuchMethodException {
        assertThat (Histogram.class.getMethod ("value").getDefaultValue (), equalTo ((Object) "#"));
    }

    @Test
    public void testBothNN () {
        Histogram annotation = annotation (Kind.Both, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (null, null);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testBothRN () {
        Histogram annotation = annotation (Kind.Both, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (response, null);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testBothNT () {
        Histogram annotation = annotation (Kind.Both, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (null, exception);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorNN () {
        Histogram annotation = annotation (Kind.Error, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (null, null);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorRN () {
        Histogram annotation = annotation (Kind.Error, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (response, null);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testErrorNT () {
        Histogram annotation = annotation (Kind.Error, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (null, exception);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testSuccessNN () {
        Histogram annotation = annotation (Kind.Success, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (null, null);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @Test
    public void testSuccessRN () {
        Histogram annotation = annotation (Kind.Success, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (response, null);

        verify (meter).update (javax.measure.Measure.valueOf (number, Unit.ONE));
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testSuccessNT () {
        Histogram annotation = annotation (Kind.Success, number);

        Aspect aspect;

        aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>absent ());
        aspect.before ();
        aspect.after  (null, exception);

        verify (meter, never ()).update (any (Measurable.class));
    }

    @Test
    public void testMetriculateNN () {
        TestMeasure test = new TestMeasure (measure);

        {
            Histogram annotation = annotation (Kind.Both, number);

            Aspect aspect;

            aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>of (test));
            aspect.before ();
            aspect.after  (null, null);

            verify (meter).update (javax.measure.Measure.valueOf (measure, Unit.ONE));
        }
    }

    @Test
    public void testMetriculateRN () {
        TestMeasure test = new TestMeasure (measure);

        {
            Histogram annotation = annotation (Kind.Both, number);

            Aspect aspect;

            aspect = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>of (test));
            aspect.before ();
            aspect.after  (response, null);

            verify (meter).update (javax.measure.Measure.valueOf (measure, Unit.ONE));
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testMetriculateNT () {
        TestMeasure test = new TestMeasure (measure);

        {
            Histogram annotation = annotation (Kind.Both, number);

            Aspect histogram;

            histogram = new MeterAspectHistogram (annotation, supplier, Optional.<Measure>of (test));
            histogram.before ();
            histogram.after (null, exception);

            verify (meter).update (javax.measure.Measure.valueOf (measure, Unit.ONE));
        }
    }

    private Histogram annotation (Kind kind, long number) {
        Histogram annotation;

              annotation = mock (Histogram.class);
        when (annotation.number ()).thenReturn (number);
        when (annotation.kind ()).thenReturn (kind);

        return annotation;
    }

}