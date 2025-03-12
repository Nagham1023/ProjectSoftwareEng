package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class updateResponse implements Serializable {
    private String newResponse;
    private int idComplain;
    private static final long serialVersionUID = -8224097662914849957L;

    public updateResponse(String newResponse, String idComplain) {
        this.newResponse = newResponse;
        this.idComplain = Integer.parseInt(idComplain);
    }
    public String getnewResponse() {
        return newResponse;
    }
    public int getIdComplain() {
        return idComplain;
    }
    public void setnewResponse(String newResponse) {
        this.newResponse = newResponse;
    }
    public void setIdComplain(int idComplain) {
        this.idComplain = idComplain;
    }
}
