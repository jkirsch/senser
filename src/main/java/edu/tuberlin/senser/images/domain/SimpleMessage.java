package edu.tuberlin.senser.images.domain;

/**
 */
public class SimpleMessage {

    String name;
    int count;

    public SimpleMessage() {
    }

    public SimpleMessage(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
