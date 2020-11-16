package com.greatfinds.cs201.tests.ws;

import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

@Named
@ViewScoped
public class PushBean implements Serializable {

    private static final AtomicLong counter = new AtomicLong();

    private boolean connected;

    @Inject
    @Push(channel = "counter")
    private PushContext push;

    public void toggle() {
        connected = !connected;
    }

    public void increment() {
        long newValue = counter.incrementAndGet();
        System.out.println("Incremented " + newValue);
        push.send(newValue);
    }

    public boolean isConnected() {
        return connected;
    }

    public Long getCount() {
        return counter.get();
    }

}