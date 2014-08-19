package com.jamierf.rsc.client.loader.client;

import com.google.common.base.Throwables;
import com.jamierf.rsc.client.error.GameClientModificationException;
import com.jamierf.rsc.client.ui.ClientFrame;
import org.eclipse.jetty.util.component.LifeCycle;

public class GameClientLifeCycleManager implements LifeCycle.Listener {

    private final GameClient client;

    public GameClientLifeCycleManager(final GameClient client) {
        this.client = client;
    }

    @Override
    public void lifeCycleStarting(final LifeCycle event) {

    }

    @Override
    public void lifeCycleStarted(final LifeCycle event) {
        try {
            client.start();
            new ClientFrame(client).setVisible(true);
        } catch (GameClientModificationException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void lifeCycleFailure(final LifeCycle event, final Throwable cause) {

    }

    @Override
    public void lifeCycleStopping(final LifeCycle event) {
        client.stop();
    }

    @Override
    public void lifeCycleStopped(final LifeCycle event) {

    }
}
