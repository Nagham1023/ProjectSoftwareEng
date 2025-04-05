package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class UserManagement implements Serializable {
    private String username;
    private String password;
    private String email;
    private int age;
    private String gender;
    private String role;
    private String method;
    private String oldName;

    //for delete the user
    public UserManagement(String username, String method) {
        this.username = username;
        this.method = method;
    }

    //for the update details
    public UserManagement(String oldName,String username, String password, String email, int age, String gender, String role, String method) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.role = role;
        this.method = method;
        this.oldName = oldName;
    }

    public UserManagement() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }
}
