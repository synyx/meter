package de.synyx.meter.example;

import de.synyx.meter.example.cdi.BambooBinder;
import de.synyx.meter.example.restlet.Bamboo;
import de.synyx.meter.jersey.MeterFeature;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 * <p>BambooApplication class.</p>
 *
 * @author Michael Clausen - clausen@synyx.de
 * @version $Id: $Id
 */
public class BambooApplication extends Application {

    /** {@inheritDoc} */
    @Override
    public Set<Object> getSingletons () {
        Set<Object> singletons = new HashSet<> ();
                    singletons.add (new BambooBinder ());

        return singletons;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Class<?>> getClasses () {
        Set<Class<?>> classes = new HashSet<> ();
                      classes.add (MeterFeature.class);
                      classes.add (Bamboo.class);

        return classes;
    }

}
