package br.com.dias.apiRest.config;

import br.com.dias.apiRest.data.dto.v1.BookDTO;
import br.com.dias.apiRest.data.dto.v1.PersonDTO;
import br.com.dias.apiRest.model.Book;
import br.com.dias.apiRest.model.Person;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setPreferNestedProperties(true);
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.createTypeMap(Person.class, PersonDTO.class)
                .addMapping(Person::getId, PersonDTO::setIdentity);

        modelMapper.createTypeMap(PersonDTO.class, Person.class)
                .addMapping(PersonDTO::getIdentity, Person::setId);

        modelMapper.createTypeMap(Book.class, BookDTO.class)
                .addMapping(Book::getId, BookDTO::setIdentity);

        return modelMapper;
    }
}
