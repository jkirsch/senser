package edu.tuberlin.senser.images.web.controller;

import edu.tuberlin.senser.images.web.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 */
@Controller
public class ImageController {

    @Autowired
    PersonRepository personRepository;

    @RequestMapping("/images/{id}")
    public String greeting(@PathVariable("id") int id, Model model) {

        model.addAttribute("person", personRepository.findOne(id));
        return "faces";
    }
}
