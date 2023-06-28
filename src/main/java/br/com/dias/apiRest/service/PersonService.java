package br.com.dias.apiRest.service;

import br.com.dias.apiRest.controller.PersonController;
import br.com.dias.apiRest.data.dto.v1.PersonDTO;
import br.com.dias.apiRest.exceptions.ResourceNotFoundException;
import br.com.dias.apiRest.model.Person;
import br.com.dias.apiRest.repository.PersonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    private ModelMapper modelMapper;

    public PersonDTO findById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        var personDTO = modelMapper.map(person, PersonDTO.class);
        personDTO.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return personDTO;
    }

    public List<PersonDTO> findAll() {
        List<Person> persons = personRepository.findAll();
        var personsDTO =  persons.stream()
                .map(person -> modelMapper.map(person, PersonDTO.class))
                .collect(Collectors.toList());

        personsDTO.forEach(person -> person.add(linkTo(methodOn(PersonController.class)
                .findById(person.getIdentity())).withSelfRel()));

        return personsDTO;
    }

    public PersonDTO create(PersonDTO person) {
        var personEntity = modelMapper.map(person, Person.class);
        var personDTO = modelMapper.map(personRepository.save(personEntity), PersonDTO.class);
        personDTO.add(linkTo(methodOn(PersonController.class)
                .findById(personDTO.getIdentity())).withSelfRel());
        return personDTO;
    }

    public PersonDTO update(PersonDTO person) {
        var entity = personRepository.findById(person.getIdentity())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var personDTO = modelMapper.map(personRepository.save(entity), PersonDTO.class);
        personDTO.add(linkTo(methodOn(PersonController.class)
                .findById(personDTO.getIdentity())).withSelfRel());
        return personDTO;
    }

    public void delete(Long id) {
        var person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        personRepository.delete(person);
    }
}
