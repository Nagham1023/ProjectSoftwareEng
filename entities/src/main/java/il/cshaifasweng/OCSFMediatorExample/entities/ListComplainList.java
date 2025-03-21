package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListComplainList implements Serializable {
    private static final long serialVersionUID = -8224097662914849956L;
    private List<Complain> complaincd;
    private List<Complain> complaincn;
    private List<Complain> complainfd;
    private List<Complain> complainfn;
    private List<Complain> complainsd;
    private List<Complain> complainsn;

    public ListComplainList(List<Complain> list1, List<Complain> list2, List<Complain> list3, List<Complain> list4, List<Complain> list5, List<Complain> list6) {
        this.complaincd = list1;
        this.complaincn = list2;
        this.complainfd = list3;
        this.complainfn = list4;
        this.complainsd = list5;
        this.complainsn = list6;
    }

    public List<Complain> getComplaincd() {
        return complaincd;
    }

    public void setComplaincd(List<Complain> complaincd) {
        this.complaincd = complaincd;
    }

    public List<Complain> getComplaincn() {
        return complaincn;
    }

    public void setComplaincn(List<Complain> complaincn) {
        this.complaincn = complaincn;
    }

    public List<Complain> getComplainfd() {
        return complainfd;
    }

    public void setComplainfd(List<Complain> complainfd) {
        this.complainfd = complainfd;
    }

    public List<Complain> getComplainfn() {
        return complainfn;
    }

    public void setComplainfn(List<Complain> complainfn) {
        this.complainfn = complainfn;
    }

    public List<Complain> getComplainsd() {
        return complainsd;
    }

    public void setComplainsd(List<Complain> complainsd) {
        this.complainsd = complainsd;
    }

    public List<Complain> getComplainsn() {
        return complainsn;
    }

    public void setComplainsn(List<Complain> complainsn) {
        this.complainsn = complainsn;
    }


    public void addComplaincd(Complain complain) {
        this.complaincd.add(complain);
    }

    public void addComplaincn(Complain complain) {
        this.complaincn.add(complain);
    }

    public void addComplainfd(Complain complain) {
        this.complainfd.add(complain);
    }

    public void addComplainfn(Complain complain) {
        this.complainfn.add(complain);
    }

    public void addComplainsn(Complain complain) {
        this.complainsn.add(complain);
    }

    public void addComplainsd(Complain complain) {
        this.complainsd.add(complain);
    }

    public void tooString() {
        for (Complain complain : complaincd) {
            System.out.println(
                    "complainEvent{" +
                            "name_complain='" + complain.getName() + '\'' +
                            ", email_complain=" + complain.getEmail() + '\'' +
                            ", kind_complain=" + complain.getKind() + '\'' +
                            ", tell_complain=" + complain.getTell() + '\'' +
                            ", date_complain=" + complain.getDate() + '\'' +
                            ", time_complain=" + complain.getTime() + '\'' +
                            ", status_complaint=" + complain.getStatus() + '\'' +
                            ", response_complaint=" + complain.getResponse() + '\'' +
                            ", restaurant=" + complain.getRestaurant() + '\'' +
                            ", orderNum_complaint=" + complain.getOrderNum() + '\'' +
                            ", refund_complaint=" + complain.getRefund() + '\'' +
                            ", Id='" + complain.getId() + '\'' +
                            '}'
            );
        }
    }
}


