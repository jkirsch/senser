package edu.tuberlin.senser.images.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tuberlin.senser.images.domain.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/**
 */
@RestController
@RequestMapping("/controller")
public class WebController {

    private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    @JmsListener(destination = "output")
    public void receiveMessage(String jsonMessage) throws IOException {

        SimpleMessage message = mapper.readValue(jsonMessage, SimpleMessage.class);

        // tell everyone
        if(message.getCount() > 1) {
            LOG.info("Received > {} <", message);
            messagingTemplate.convertAndSend("/topic/stats",
                    message);
        }

    }

    @Value("${twitter.trackedTerms}")
    private String[] trackedTerms;

    @Value("${twitter.enabled}")
    private boolean trackTwitter;

    @RequestMapping(value = "/trackedTerms")
    public String[] keywords() {
        return trackTwitter?trackedTerms:new String[]{"Tracking Faces in Live Video"};
    }


}
