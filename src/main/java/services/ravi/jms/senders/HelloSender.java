package services.ravi.jms.senders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import services.ravi.jms.configs.JmsConfig;
import services.ravi.jms.model.HelloWorldMessage;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

//    @Scheduled(fixedRate = 2000)
//    public void sendMessage(){
//        System.out.println("I'm sending a message...");
//        HelloWorldMessage message = HelloWorldMessage.builder()
//                                    .id(UUID.randomUUID())
//                                    .message("Hello World")
//                                    .build();
//        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE ,message);
//    }

    @Scheduled(fixedRate = 2000)
    public void sendMessage() throws JMSException {
        System.out.println("I'm sending a message...");
        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();

        Message receivedMessage = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RECEIVE_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;
                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "services.ravi.jms.model.HelloWorldMessage");

                    System.out.println("Sending Hello...");

                    return helloMessage;
                } catch (JsonProcessingException e) {
                    throw new JMSException("BOOOMM");
                }
            }
        });

        System.out.println("Received Message: "+receivedMessage.getBody(String.class));
    }

}
