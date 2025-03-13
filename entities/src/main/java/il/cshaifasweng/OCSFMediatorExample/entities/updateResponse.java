package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class updateResponse implements Serializable {
    private String newResponse;
    private int idComplain;
    private String emailComplain;
    private String orderNumber;
    private double refundAmount;
    //private static final long serialVersionUID = -8224097662914849957L;

    public updateResponse(String newResponse, String idComplain, String email, String orderNum, double refund) {
        this.newResponse = newResponse;
        this.idComplain = Integer.parseInt(idComplain);
        this.emailComplain = email;
        this.orderNumber = orderNum;
        this.refundAmount = refund;
    }
    public String getnewResponse() {
        return newResponse;
    }
    public void setnewResponse(String newResponse) {
        this.newResponse = newResponse;
    }

    public int getIdComplain() {
        return idComplain;
    }
    public void setIdComplain(int idComplain) {
        this.idComplain = idComplain;
    }

    public String getEmailComplain() {return emailComplain;}
    public void setEmailComplain(String emailComplain) {this.emailComplain = emailComplain;}

    public String getOrderNumber() {return orderNumber;}
    public void setOrderNumber(String orderNumber) {this.orderNumber = orderNumber;}

    public double getRefundAmount() {return refundAmount;}
    public void setRefundAmount(double refundAmount) {this.refundAmount = refundAmount;}
}
