package br.com.dias.apiRest.service;

import br.com.dias.apiRest.data.dto.v1.PersonDTO;
import br.com.dias.apiRest.exceptions.ResourceNotFoundException;
import br.com.dias.apiRest.model.Person;
import br.com.dias.apiRest.repository.PersonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
        return modelMapper.map(person, PersonDTO.class);
    }

    public List<PersonDTO> findAll() {
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(person -> modelMapper.map(person, PersonDTO.class))
                .collect(Collectors.toList());
    }

    public PersonDTO create(PersonDTO person) {
        Person personEntity = modelMapper.map(person, Person.class);
        Person savedPerson = personRepository.save(personEntity);
        return modelMapper.map(savedPerson, PersonDTO.class);
        // return modelMapper.map(personRepository.save(personEntity), PersonDTO.class);
    }

    public PersonDTO update(PersonDTO person) {
        Person entity = personRepository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        personRepository.save(entity);
        return modelMapper.map(entity, PersonDTO.class);
    }

    public void delete(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        personRepository.delete(person);
    }
}
