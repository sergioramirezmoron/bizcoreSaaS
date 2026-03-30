package com.bizcore.notifications.infrastructure.adapter;

import com.bizcore.notifications.domain.port.out.WhatsAppPort;
import com.bizcore.notifications.infrastructure.config.TwilioProperties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TwilioWhatsAppAdapter implements WhatsAppPort {

    private final TwilioProperties twilioProperties;

    @Override
    public void send(String toPhone, String message) {
        String sid   = twilioProperties.getAccountSid();
        String token = twilioProperties.getAuthToken();
        String from  = twilioProperties.getFromNumber();

        if (sid == null || sid.isBlank() || token == null || token.isBlank()) {
            log.warn("Twilio no configurado. WhatsApp a {} omitido.", toPhone);
            return;
        }

        Twilio.init(sid, token);

        // Twilio WhatsApp usa el prefijo "whatsapp:" en el número
        String whatsappTo   = toPhone.startsWith("whatsapp:") ? toPhone : "whatsapp:" + toPhone;
        String whatsappFrom = from.startsWith("whatsapp:")   ? from    : "whatsapp:" + from;

        Message.creator(
                new PhoneNumber(whatsappTo),
                new PhoneNumber(whatsappFrom),
                message
        ).create();

        log.debug("WhatsApp enviado a {}", toPhone);
    }
}
