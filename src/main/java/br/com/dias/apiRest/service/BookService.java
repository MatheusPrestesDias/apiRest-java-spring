package br.com.dias.apiRest.service;

import br.com.dias.apiRest.controller.BookController;
import br.com.dias.apiRest.controller.PersonController;
import br.com.dias.apiRest.data.dto.v1.BookDTO;
import br.com.dias.apiRest.data.dto.v1.PersonDTO;
import br.com.dias.apiRest.exceptions.ResourceNotFoundException;
import br.com.dias.apiRest.model.Book;
import br.com.dias.apiRest.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PagedResourcesAssembler<BookDTO> assembler;

    public BookDTO findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        var bookDTO = modelMapper.map(book, BookDTO.class);

        bookDTO.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());

        return bookDTO;
    }

    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {
        var books = bookRepository.findAll(pageable);

        var booksDTO = books.map(
                book -> modelMapper.map(book, BookDTO.class));

        booksDTO.map(book -> book.add(linkTo(methodOn(BookController.class)
                .findById(book.getIdentity())).withSelfRel()));

        Link link = linkTo(methodOn(BookController.class).findAll(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                "asc")).withSelfRel();
        
        return assembler.toModel(booksDTO, link);
    }

    public BookDTO create(BookDTO book) {
        var bookEntity = modelMapper.map(book, Book.class);
        var bookDTO = modelMapper.map(bookRepository.save(bookEntity), BookDTO.class);

        bookDTO.add(linkTo(methodOn(BookController.class)
                .findById(bookDTO.getIdentity())).withSelfRel());

        return bookDTO;
    }

    public BookDTO update(BookDTO book) {
        var entity = bookRepository.findById(book.getIdentity())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var bookDTO = modelMapper.map(bookRepository.save(entity), BookDTO.class);

        bookDTO.add(linkTo(methodOn(BookController.class)
                .findById(bookDTO.getIdentity())).withSelfRel());

        return bookDTO;
    }

    public void delete(Long id) {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        bookRepository.delete(book);
    }
}
