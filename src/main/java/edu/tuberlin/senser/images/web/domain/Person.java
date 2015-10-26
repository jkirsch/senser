package edu.tuberlin.senser.images.web.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Person in the database.
 */
@Entity
public class Person {

    /**
     * Unique identifier.
     */
    @Id
    int id;

    /**
     * Name of the Person.
     */
    @Column(nullable = false)
    String name;

    /**
     * List of Images associated with this person.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<FaceImage> images = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<FaceImage> getImages() {
        return images;
    }

    public void setImages(Set<FaceImage> images) {
        this.images = images;
    }
}
