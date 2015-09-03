package edu.tuberlin.senser.images.flink.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import edu.tuberlin.senser.images.domain.Tweet;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 */
@Component
@ConfigurationProperties(locations = "${twitter.authfile}", ignoreUnknownFields = true, prefix = "twitter")
public class TwitterSource implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterSource.class);

    @Autowired
    JmsTemplate jmsTemplate;

    ObjectMapper mapper = new ObjectMapper();

    @NotBlank
    private String consumerKey;
    @NotBlank
    private String consumerSecret;
    @NotBlank
    private String token;
    @NotBlank
    private String secret;

    protected OAuth1 authenticate() {
        return new OAuth1(consumerKey, consumerSecret, token, secret);
    }

    @Override
    public void run() {
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        // Optional: set up some followings and track terms
        List<String> terms = Lists.newArrayList("berlin");
        hosebirdEndpoint.trackTerms(terms);


        // These secrets should be read from a config file
        Authentication hosebirdAuth = authenticate();

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

        Client hosebirdClient = builder.build();
        // Attempts to establish a connection.
        hosebirdClient.connect();

        // on a different thread, or multiple different threads....
        while (!hosebirdClient.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.take();

                Tweet tweet = mapper.readValue(msg, Tweet.class);

                //jmsTemplate.convertAndSend("input", tweet.text);

                // Publish the language
                jmsTemplate.convertAndSend("input", tweet.lang);

/*
                if (tweet.entities != null) {

                    tweet.entities.hashtags.forEach(
                            new Consumer<Tweet.HashTag>() {
                                @Override
                                public void accept(Tweet.HashTag hashTag) {
                                    jmsTemplate.convertAndSend("input", hashTag.text);
                                }
                            }
                    );
                }*/


            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    @PostConstruct
    private void start() {
        new Thread(this).start();
    }


    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
