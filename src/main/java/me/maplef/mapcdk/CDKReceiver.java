package me.maplef.mapcdk;

import java.time.LocalDateTime;

public class CDKReceiver {
    private String receiverName;
    private LocalDateTime receiveTime;

    public CDKReceiver() {}

    public CDKReceiver(String name, LocalDateTime time) {
        this.receiverName = name;
        this.receiveTime = time;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public LocalDateTime getReceiveTime() {
        return receiveTime;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiveTime(LocalDateTime receiveTime) {
        this.receiveTime = receiveTime;
    }
}
