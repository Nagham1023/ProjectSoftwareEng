package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ComplainList implements Serializable {

    private List<Complain> ComplainList;

    public ComplainList() {
        ComplainList = new ArrayList<Complain>();
    }
    public List<Complain> getComplainList() {
        return ComplainList;
    }
    public void setComplainList(List<Complain> ComplainList) {
        this.ComplainList = ComplainList;
    }

    @Override
    public String toString() {
        if (ComplainList.isEmpty()) {
            return "";
        }
        StringBuilder ComplanintNames = new StringBuilder("ComplainList: ");
        for (Complain comp : ComplainList) {
            ComplanintNames.append(comp.getName()).append(", ");
        }
        // Remove the trailing comma and space
        if (ComplanintNames.length() > 0) {
            ComplanintNames.setLength(ComplanintNames.length() - 2);
        }
        return ComplanintNames.toString();
    }
}
