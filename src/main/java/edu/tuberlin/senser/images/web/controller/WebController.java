package edu.tuberlin.senser.images.web.controller;

import edu.tuberlin.senser.images.domain.SimpleMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 */
@RestController
@RequestMapping("/controller")
public class WebController {

    private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @JmsListener(destination = "output")
    public void receiveMessage(String message) {

        LOG.info("Received > {} <", message);

        String[] split = message.split(",");

        String name = StringUtils.removeStart(split[0], "(");
        int number = Integer.parseInt(StringUtils.removeEnd(split[1], ")"));

        // tell everyone
        if(number > 1) {
            messagingTemplate.convertAndSend("/topic/stats",
                    new SimpleMessage(name, number));
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
