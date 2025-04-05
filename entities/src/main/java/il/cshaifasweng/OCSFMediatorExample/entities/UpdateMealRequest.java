package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class UpdateMealRequest implements Serializable {
    String mealId;
    String newDescription;
    List<String> newCustomizations;
    List<String> newBranches;
    String branchName;

    public UpdateMealRequest(String mealId, String newDescription, List<String> newCustomizations, String branchName) {
        this.mealId = mealId;
        this.newDescription = newDescription;
        this.newCustomizations = newCustomizations;
        this.branchName = branchName;
    }

    public UpdateMealRequest(String mealId, String newDescription, List<String> newCustomizations, List<String> newBranches) {
        this.mealId = mealId;
        this.newDescription = newDescription;
        this.newCustomizations = newCustomizations;
        this.newBranches = newBranches;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public List<String> getNewCustomizations() {
        return newCustomizations;
    }

    public void setNewCustomizations(List<String> newCustomizations) {
        this.newCustomizations = newCustomizations;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public List<String> getNewBranches() {
        return newBranches;
    }

    public void setNewBranches(List<String> newBranches) {
        this.newBranches = newBranches;
    }
}
