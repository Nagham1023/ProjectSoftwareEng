package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class specificComplains implements Serializable {
    private String Kind;
    private String status;

    public specificComplains(String kind, String status) {
        this.Kind = kind;
        this.status = status;
    }
    public specificComplains() {}

    public String getSpecificKind() {
        return Kind;
    }
    public void setSpecificKind(String kind) {
        this.Kind = kind;
    }
    public String getSpecificStatus() {
        return status;
    }
    public void setSpecificStatus(String status) {
        this.status = status;
    }
}
