package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class newOrderAdded implements Serializable {
    private Order order;
    public newOrderAdded(Order order) {
        this.order = order;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
}
