package edu.tuberlin.senser.images.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
public class Tweet {
    public String text;
    int retweet_count;
    int favorite_count;
    public String lang;

    public TwitterEntities entities;

    @JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
    public static class TwitterEntities {

        public List<HashTag> hashtags;

    }

    @JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
    public static class HashTag {
        public String text;


    }

}