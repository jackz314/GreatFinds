package entry_test;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.annotation.FacesConfig;
import javax.faces.context.FacesContext;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

// enforce JSF 2.3
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
@Named
@ApplicationScoped
public class EntryBean {

    private Entry inputEntry;

    private List<Entry> entries;

    @Inject
    private EntryNotificationService service;

    @Inject @Push
    private PushContext pushCh;

    @PostConstruct
    public void load() {
//        String version = getJSFVersion();
//        System.out.println("JSF Version: " + version);
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

    public void onNewEntry(@Observes Entry newEntry) {
        System.out.println("NEW ENTRY: " + newEntry);
        entries.add(0, newEntry);
        pushCh.send("updateEntries");
//        comm.PushEP.sendAll("updateEntries");
    }

    public List<Entry> getEntries() {
//        entries = new LinkedList<>();
//        entries.add(new entry_test.Entry("Hello"));
//        entries.add(new entry_test.Entry("Test"));
        System.out.println("ENTRIES: " + entries);
        return entries;
    }

}