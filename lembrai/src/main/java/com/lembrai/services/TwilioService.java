package com.lembrai.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.fromPhone}")
    private String fromPhone;

    @Value("${My.Number}")
    private String myNumber;

    public void enviarMensagemWhatsApp(String mensagem) {
        Twilio.init(accountSid, authToken);  // inicializa o cliente
        Message.creator(
                new com.twilio.type.PhoneNumber(myNumber),
                new com.twilio.type.PhoneNumber(fromPhone),
                mensagem
        ).create();
    }
}
