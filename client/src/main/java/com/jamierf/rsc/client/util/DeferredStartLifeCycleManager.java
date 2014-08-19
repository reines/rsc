package com.jamierf.rsc.client.util;

import com.google.common.base.Throwables;
import io.dropwizard.lifecycle.Managed;
import org.eclipse.jetty.util.component.LifeCycle;

public class DeferredStartLifeCycleManager implements LifeCycle.Listener {

    private final Managed delegate;

    public DeferredStartLifeCycleManager(final Managed delegate) {
        this.delegate = delegate;
    }

    @Override
    public void lifeCycleStarting(final LifeCycle event) {
        
    }

    @Override
    public void lifeCycleStarted(final LifeCycle event) {
        try {
            delegate.start();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void lifeCycleFailure(final LifeCycle event, final Throwable cause) {

    }

    @Override
    public void lifeCycleStopping(final LifeCycle event) {
        try {
            delegate.stop();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void lifeCycleStopped(final LifeCycle event) {

    }
}
