package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchOptions implements Serializable {
    private List<String> restaurantNames;
    private List<String> customizationNames;
    private String branchName; // Single branch name instead of a list

    // Constructor
    public SearchOptions(List<String> restaurantNames, List<String> customizationNames, String branchName) {
        this.restaurantNames = restaurantNames;
        this.customizationNames = customizationNames;
        this.branchName = branchName;
    }
    public SearchOptions(List<String> restaurantNames, List<String> customizationNames) {
        this.restaurantNames = restaurantNames;
        this.customizationNames = customizationNames;
    }
    public SearchOptions( List<String> restaurantNames,String branchName) {
        this.restaurantNames = restaurantNames;
        this.customizationNames = new ArrayList<String>();
        this.branchName = branchName;
    }



    // Getters and setters
    public List<String> getRestaurantNames() {
        return restaurantNames;
    }

    public void setRestaurantNames(List<String> restaurantNames) {
        this.restaurantNames = restaurantNames;
    }

    public List<String> getCustomizationNames() {
        return customizationNames;
    }

    public void setCustomizationNames(List<String> customizationNames) {
        this.customizationNames = customizationNames;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    // Override toString() for debugging
    @Override
    public String toString() {
        return "SearchOptions{" +
                "restaurantNames=" + restaurantNames +
                ", customizationNames=" + customizationNames +
                ", branchName='" + branchName + '\'' +
                '}';
    }
}
