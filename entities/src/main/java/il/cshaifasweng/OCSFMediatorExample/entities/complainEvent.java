package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class complainEvent implements Serializable {


    private int id;
    private String kind_complain;
    private String name_complain;
    private String email_complain;
    private String tell_complain;
    private LocalDate date_complain;
    private LocalDateTime time_complain;
    private Restaurant restaurant;
    private String status_complaint;
    private String response_complaint;
    private String orderNum_complaint="";
    private double refund_complaint=0;


    public complainEvent(String kind, String name, String email, String tell,int id,LocalDate date,LocalDateTime time, Restaurant restaurant,String status, String response, String orderNum, double refund) {
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
        this.orderNum_complaint = orderNum;
        this.refund_complaint = refund;
    }

    public complainEvent(String kind, String name, String email, String tell ,LocalDate date,LocalDateTime time, Restaurant restaurant,String status, String response, String orderNum, double refund){
        this.kind_complain = kind;
        this.name_complain = name;
        this.email_complain = email;
        this.tell_complain = tell;
        this.date_complain = date;
        this.time_complain = time;
        this.status_complaint = status;
        this.response_complaint = response;
        this.restaurant = restaurant;
        this.orderNum_complaint = orderNum;
        this.refund_complaint = refund;
    }

    public complainEvent() {
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

    public String getKind() {return kind_complain;}
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

    public LocalDate getDate() {
        return date_complain;
    }
    public void setDate(LocalDate date) {this.date_complain = date;}

    public LocalDateTime getTime() {return time_complain;}
    public void setTime(LocalDateTime time) {this.time_complain = time;}

    public Restaurant getRestaurant() {return restaurant;}
    public void setRestaurant(Restaurant restaurant) {this.restaurant = restaurant;}

    public String getStatus() {return status_complaint;}
    public void setStatus(String status) {this.status_complaint = status;}

    public String getResponse() {return response_complaint;}
    public void setResponse(String response) {this.response_complaint = response;}

    public String getOrderNum() {return orderNum_complaint;}
    public void setOrderNum(String orderNum) {this.orderNum_complaint = orderNum;}

    public double getRefund() {return refund_complaint;}
    public void setRefund(double refund) {this.refund_complaint = refund;}

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
                ", orderNum_complaint=" + orderNum_complaint + '\'' +
                ", refund_complaint=" + refund_complaint + '\'' +
                ", Id='" + id + '\'' +
                '}';
    }
}