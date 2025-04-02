package il.cshaifasweng.OCSFMediatorExample.client.events;

import java.io.Serializable;
import java.time.LocalTime;

public class DeleteMealEvent implements Serializable {
    private String id;
    private String mealName;
    private LocalTime time;

    public DeleteMealEvent(String id,String mealName) {
        this.id = id;
        this.mealName = mealName;
        this.time = LocalTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
