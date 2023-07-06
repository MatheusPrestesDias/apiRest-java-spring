package br.com.dias.apiRest.unittests.service;

import br.com.dias.apiRest.data.dto.v1.PersonDTO;
import br.com.dias.apiRest.model.Person;
import br.com.dias.apiRest.repository.PersonRepository;
import br.com.dias.apiRest.service.PersonService;
import br.com.dias.apiRest.unittests.mock.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    private MockPerson input;

    @InjectMocks
    private PersonService service;

    @Mock
    private PersonRepository repository;

    @Spy
    private ModelMapper modelMapper;

    @BeforeEach
    void setupMocks() throws Exception {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
        modelMapper.createTypeMap(Person.class, PersonDTO.class)
                .addMapping(Person::getId, PersonDTO::setIdentity);
        modelMapper.createTypeMap(PersonDTO.class, Person.class)
                .addMapping(PersonDTO::getIdentity, Person::setId);
    }

    @Test
    void findById() {
        Person entity = input.mockEntity(1);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var result = service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getIdentity());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void findAll() {
        List<Person> listEntity = input.mockEntityList();

        when(repository.findAll()).thenReturn(listEntity);

        var result = service.findAll();
        assertNotNull(result);
        assertEquals(14, result.size());
    }

    @Test
    void create() {
        Person entity = input.mockEntity(1);

        PersonDTO personDTO = input.mockDTO(1);

        when(repository.save(entity)).thenReturn(entity);

        var result = service.create(personDTO);
        assertNotNull(result);
        assertNotNull(result.getIdentity());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void update() {
        Person entity = input.mockEntity(1);

        PersonDTO personDTO = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        var result = service.update(personDTO);
        assertNotNull(result);
        assertNotNull(result.getIdentity());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void delete() {
        Person entity = input.mockEntity(1);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(entity.getId());
    }
}