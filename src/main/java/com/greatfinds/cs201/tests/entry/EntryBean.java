package com.greatfinds.cs201.tests.entry;

import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.faces.annotation.FacesConfig;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

// enforce JSF 2.3
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
@Named
@RequestScoped
public class EntryBean {

    private Entry inputEntry;

    private List<Entry> entries;

    @Inject
    private EntryNotificationService service;

    @Inject @Push
    private PushContext pushCh;

    @PostConstruct
    public void load() {
        inputEntry = new Entry();
        entries = service.list();
    }

    public Entry getInputEntry(){
        return inputEntry;
    }

    public void submit(){
        service.create(inputEntry.getMsg());
        inputEntry.setMsg("");
    }

    public String getJSFVersion() {
        return FacesContext.class.getPackage().getImplementationVersion();
    }

    public double getRandNum(){
        double r = Math.random();
        System.out.println(r);
        return r;
    }

    //called when a new entry appears in the database
    public void onNewEntry(@Observes Entry newEntry) {
        System.out.println("NEW ENTRY: " + newEntry);
        entries.add(0, newEntry);
        pushCh.send("updateEntries");
    }

    public List<Entry> getEntries() {
        return entries;
    }

}