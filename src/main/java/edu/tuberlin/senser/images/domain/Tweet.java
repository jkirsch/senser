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

    TwitterEntities entities;

    @JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
    public static class TwitterEntities {

        List<HashTag> hashtags;

        public List<HashTag> getHashtags() {
            return hashtags;
        }

        @Override
        public String toString() {
            return "TwitterEntities{" +
                    "hashtags=" + hashtags +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true) // so we only include ones we care about
    public static class HashTag {
        String text;

        public String getText() {
            return text;
        }

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

    public TwitterEntities getEntities() {
        return entities;
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