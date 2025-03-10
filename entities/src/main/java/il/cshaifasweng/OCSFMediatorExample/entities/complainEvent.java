package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class complainEvent implements Serializable {


    private int id;
    private String kind_complain;
    private String name_complain;
    private String email_complain;
    private String tell_complain;
    private Date date_complain;
    private Time time_complain;
    private String status_complaint;
    private String response_complaint;
    private Restaurant restaurant;


    public complainEvent(String kind, String name, String email, String tell,int id,Date date,Time time, String status, String response, Restaurant restaurant) {
        this.kind_complain = kind;
        this.name_complain = name;
        this.email_complain = email;
        this.tell_complain = tell;
        this.id = id;
        this.date_complain = date;
        this.time_complain = time;
        this.status_complaint = status;
        this.response_complaint = response;
        this.restaurant = restaurant;
    }

    public complainEvent(String kind, String name, String email, String tell,Date date,Time time, String status, String response, Restaurant restaurant) {
        this.kind_complain = kind;
        this.name_complain = name;
        this.email_complain = email;
        this.tell_complain = tell;
        this.date_complain = date;
        this.time_complain = time;
        this.status_complaint = status;
        this.response_complaint = response;
        this.restaurant = restaurant;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name_complain;
    }
    public void setName(String name) {
        this.name_complain = name;
    }

    public String getKind() {
        return kind_complain;
    }
    public void setKind(String kind) {
        this.kind_complain = kind;
    }

    public String getEmail() {
        return email_complain;
    }
    public void setEmail(String email) {
        this.email_complain = email;
    }

    public String getTell() {
        return tell_complain;
    }
    public void setTell(String tell) {
        this.tell_complain = tell;
    }

    public Date getDate() {
        return date_complain;
    }
    public void setDate(Date date) {this.date_complain = date;}

    public Time getTime() {return time_complain;}
    public void setTime(Time time) {this.time_complain = time;}

    public Restaurant getRestaurant() {return restaurant;}
    public void setRestaurant(Restaurant restaurant) {this.restaurant = restaurant;}

    public String getStatus() {return status_complaint;}
    public void setStatus(String status) {this.status_complaint = status;}

    public String getResponse() {return response_complaint;}
    public void setResponse(String response) {this.response_complaint = response;}

    @Override
    public String toString() {
        return "complainEvent{" +
                "name_complain='" + name_complain + '\'' +
                ", email_complain=" + email_complain + '\'' +
                ", kind_complain=" + kind_complain + '\'' +
                ", tell_complain=" + tell_complain + '\'' +
                ", date_complain=" + date_complain + '\'' +
                ", time_complain=" + time_complain + '\'' +
                ", status_complaint=" + status_complaint + '\'' +
                ", response_complaint=" + response_complaint + '\'' +
                ", restaurant=" + restaurant + '\'' +
                ", Id='" + id + '\'' +
                '}';
    }
}