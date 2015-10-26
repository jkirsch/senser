package edu.tuberlin.senser.images.web.repository;

import edu.tuberlin.senser.images.web.domain.Person;
import org.springframework.data.repository.CrudRepository;

/**
 * The databse backend to manipulate the Person interface.
 */
public interface PersonRepository extends CrudRepository<Person, Integer> {

}
