package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Users;

import java.io.Serializable;
import java.util.List;

public class UsersListEvent implements Serializable {
    private List<Users> users;

    public UsersListEvent(List<Users> users) {
        this.users = users;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }
}
