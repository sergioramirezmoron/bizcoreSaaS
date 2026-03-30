package com.bizcore.notifications.domain.port.out;

public interface WhatsAppPort {
    void send(String toPhone, String message);
}
