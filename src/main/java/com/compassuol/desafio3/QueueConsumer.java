package com.compassuol.desafio3;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {
    private final JmsTemplate jmsTemplate;

    public QueueConsumer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "filaA")
    public void consumeFromQueueA(String message) {
        System.out.println("Recebido da filaA: " + message);
        // Processamento na filaA
        message += " (processado na filaA)";
        // Encaminha a mensagem para a próxima fila (filaB)
        sendMessageToQueueB(message);
    }

    public void sendMessageToQueueB(String message) {
        jmsTemplate.convertAndSend("filaB", message);
    }

    @JmsListener(destination = "filaB")
    public void consumeFromQueueB(String message) {
        System.out.println("Recebido da filaB: " + message);
        // Processamento na filaB
        message += " (processado na filaB)";

        // Encaminha a mensagem para a próxima fila (filaC)
        sendMessageToQueueC(message);
    }

    public void sendMessageToQueueC(String message) {
        jmsTemplate.convertAndSend("filaC", message);
    }

    @JmsListener(destination = "filaC")
    public void consumeFromQueueC(String message) {
        System.out.println("Recebido da filaC: " + message);
        // Processamento final na filaC
    }

}
