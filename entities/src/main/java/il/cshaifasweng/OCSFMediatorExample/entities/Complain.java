package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


@Entity
@Table(name = "complains")
public class Complain implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String kind_complain;
    private String name_complain;
    private String email_complain;
    private String tell_complain;
    private Date date_complain;
    private Time time_complain;
    private String status_complaint;
    private String response_complaint;


    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;


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
    public void setResponse(String response) {this.response_complaint=response;}

}