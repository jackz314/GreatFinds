
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.annotation.FacesConfig;
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
    private User loginUser;
    private boolean isUserLoggedIn = false;
    private boolean userMatched = true;

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
        loginUser = new User();
        users = service.list();
    }

    public User getInputUser(){
        return inputUser;
    }

    public User getLoginUser() {
        return loginUser;
    }

    public void register(){
        service.create(inputUser.getDisplayName(), inputUser.getEmail(), inputUser.getPwd());
        inputUser.setDisplayName("");
        inputUser.setEmail("");
        inputUser.setPwd("");
    }

    public void login(){
        userMatched = isUserLoggedIn = service.userMatch(loginUser.getEmail(), loginUser.getPwd());
    }

    public String getLoginStatus(){
        String status = isUserLoggedIn ? "logged in!" : "not logged in.";
        if(!userMatched) status += " user not found or password is incorrect";
        return status;
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