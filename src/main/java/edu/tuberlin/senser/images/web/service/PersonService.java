package edu.tuberlin.senser.images.web.service;

import edu.tuberlin.senser.images.web.domain.FaceImage;
import edu.tuberlin.senser.images.web.domain.Person;
import edu.tuberlin.senser.images.web.repository.PersonRepository;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.bytedeco.javacpp.opencv_imgcodecs.imencode;


/**
 * This is the service class for finding faces.
 */
@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    @Transactional
    public String registerImage(int personID, opencv_core.Mat face_resized, int counter, double confidence) {

        BytePointer outputPointer = new BytePointer();
        imencode(".jpg", face_resized, outputPointer);
        byte[] outputBuffer = new byte[(int) outputPointer.limit()];
        outputPointer.get(outputBuffer);

        Optional<Person> dbperson = personRepository.findById(personID);

        Person person = dbperson.orElseGet(() -> {
            Person newPerson = new Person();
            newPerson.setName("Someone " + counter);
            newPerson.setId(counter);
            return newPerson;
        });

        if(confidence > 20) {
            person.getImages().add(new FaceImage(outputBuffer));
            personRepository.save(person);
        }


        return person.getName();
    }
}
