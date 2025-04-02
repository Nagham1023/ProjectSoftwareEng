package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;

import java.time.LocalTime;

public class UpdatePriceRequestEvent {
    private updatePrice request;
    private LocalTime time;

    public UpdatePriceRequestEvent(updatePrice request) {
        this.request = request;
        this.time = LocalTime.now();
    }

    public updatePrice getRequest() {
        return request;
    }

    public void setRequest(updatePrice request) {
        this.request = request;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return  request.toString();
    }
}
