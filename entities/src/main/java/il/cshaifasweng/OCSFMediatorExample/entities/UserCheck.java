package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class UserCheck implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String email;
    private int age;
    private String gender;

    private String respond;
    private int state; //1 if login, 0 if register , 2 if forget pass, 3 if username check
    public UserCheck() {}
    public UserCheck(String username, String password,int state) {
        this.username = username;
        this.password = password;
        this.state = state;
    }
    public UserCheck(String username,int state) {
        this.username = username;
        this.state = state;
    }
    public UserCheck(String username, String password, String email, int age, String gender, int state) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.state = state;
    }
    public String getRespond(){
        return respond;
    }
    public void setRespond(String respond){
        this.respond = respond;
    }
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
    public int isState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
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

}
