
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
public class UserBean {

    private User inputUser;

    private List<User> users;

    @Inject
    private UserNotificationService service;

    @Inject @Push
    private PushContext pushCh;

    @PostConstruct
    public void load() {
//        String version = getJSFVersion();
//        System.out.println("JSF Version: " + version);
        inputUser = new User();
        users = service.list();
    }

    public User getInputUser(){
        return inputUser;
    }

    public void submit(){
        service.create(inputUser.getDisplayName(), inputUser.getEmail(), inputUser.getPwd());
        inputUser.setDisplayName("");
        inputUser.setEmail("");
        inputUser.setPwd("");
    }

    public String getJSFVersion() {
        return FacesContext.class.getPackage().getImplementationVersion();
    }

    public double getRandNum(){
        double r = Math.random();
        System.out.println(r);
        return r;
    }

    public void onNewUser(@Observes User newUser) {
        System.out.println("NEW USER: " + newUser);
        users.add(0, newUser);
        pushCh.send("updateUsers");
//        comm.PushEP.sendAll("updateEntries");
    }

    public List<User> getUsers() {
//        entries = new LinkedList<>();
//        entries.add(new user.User("Hello"));
//        entries.add(new user.User("Test"));
//        System.out.println("USERS: " + users);
        return users;
    }

}