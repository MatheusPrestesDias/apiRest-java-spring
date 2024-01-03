package br.com.dias.apiRest.service;

import br.com.dias.apiRest.controller.PersonController;
import br.com.dias.apiRest.data.dto.v1.PersonDTO;
import br.com.dias.apiRest.exceptions.ResourceNotFoundException;
import br.com.dias.apiRest.model.Person;
import br.com.dias.apiRest.repository.PersonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PagedResourcesAssembler<PersonDTO> assembler;

    public PersonDTO findById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        var personDTO = modelMapper.map(person, PersonDTO.class);
        personDTO.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return personDTO;
    }

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        var persons = personRepository.findAll(pageable);

        var personsDTO = persons.map(
                person -> modelMapper.map(person, PersonDTO.class));

        personsDTO.map(person -> person.add(linkTo(methodOn(PersonController.class)
                .findById(person.getIdentity())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findAll(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                "asc")).withSelfRel();
        return assembler.toModel(personsDTO,link);
    }

    public PagedModel<EntityModel<PersonDTO>> findPersonsByName(String firstName, Pageable pageable) {
        var persons = personRepository.findPersonsByName(firstName, pageable);

        var personsDTO = persons.map(
                person -> modelMapper.map(person, PersonDTO.class));

        personsDTO.map(person -> person.add(linkTo(methodOn(PersonController.class)
                .findById(person.getIdentity())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findAll(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                "asc")).withSelfRel();
        return assembler.toModel(personsDTO,link);
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

    @Transactional
    public PersonDTO disablePerson(Long id) {
        personRepository.disablePerson(id);
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        var personDTO = modelMapper.map(person, PersonDTO.class);
        personDTO.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return personDTO;
    }

    public void delete(Long id) {
        var person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        personRepository.delete(person);
    }
}
