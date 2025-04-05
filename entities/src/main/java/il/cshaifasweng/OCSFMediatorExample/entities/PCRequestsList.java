package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class PCRequestsList implements Serializable {
    private static final long serialVersionUID = -8224097662914849956L;
    private List<MealUpdateRequest> reqs;
    public PCRequestsList(List<MealUpdateRequest> requests) {
        this.reqs = requests;
    }

    public List<MealUpdateRequest> getReqs() {
        return reqs;
    }

    public void setReqs(List<MealUpdateRequest> reqs) {
        this.reqs = reqs;
    }
}
