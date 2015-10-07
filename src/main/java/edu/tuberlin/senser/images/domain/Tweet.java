package edu.tuberlin.senser.images.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
public class Tweet {
    public String text;
    int retweet_count;
    int favorite_count;
    public String lang;

    public Coordinate coordinates;

    public TwitterEntities entities;

    @JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
    public static class TwitterEntities {

        public List<HashTag> hashtags;

        @Override
        public String toString() {
            return "TwitterEntities{" +
                    "hashtags=" + hashtags +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
    public static class HashTag {
        public String text;

        @Override
        public String toString() {
            return "HashTag{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
    public static class Coordinate {

        String type;

        float[] coordinates;

        @Override
        public String toString() {
            return "Coordinate{" +
                    "type='" + type + '\'' +
                    ", coordinates=" + Arrays.toString(coordinates) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "text='" + text + '\'' +
                ", retweet_count=" + retweet_count +
                ", favorite_count=" + favorite_count +
                ", lang='" + lang + '\'' +
                ", entities=" + entities +
                '}';
    }
}