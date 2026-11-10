package com.example.bookapi.controller;

import com.example.bookapi.model.Book;
import com.example.bookapi.repository.BookRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final int MAX_BULK_SIZE = 500;

    private final BookRepository bookRepository;
    private final Validator validator;

    public BookController(BookRepository bookRepository, Validator validator) {
        this.bookRepository = bookRepository;
        this.validator = validator;
    }

    private ResponseEntity<Map<String, String>> notFound(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Book not found with id " + id));
    }

    // GET /api/books -> list all books
    @GetMapping
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    // GET /api/books/1 -> get one book
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            return notFound(id);
        }
        return ResponseEntity.ok(book.get());
    }

    // POST /api/books -> create a book
    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody Book book) {
        book.setId(null); // let the database generate the id
        return ResponseEntity.status(HttpStatus.CREATED).body(bookRepository.save(book));
    }

    // POST /api/books/bulk -> create many books in one request
    @PostMapping("/bulk")
    public ResponseEntity<?> createBulk(@RequestBody List<Book> books) {
        if (books == null || books.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Book list is empty"));
        }
        if (books.size() > MAX_BULK_SIZE) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Maximum " + MAX_BULK_SIZE + " books per request"));
        }

        // Validate every book first; save nothing if any book is invalid
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            Set<ConstraintViolation<Book>> violations = validator.validate(books.get(i));
            for (ConstraintViolation<Book> v : violations) {
                errors.add("Book at index " + i + ": " + v.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        books.forEach(b -> b.setId(null));
        List<Book> saved = bookRepository.saveAll(books);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", saved.size() + " books created",
                "books", saved
        ));
    }

    // PUT /api/books/1 -> update a book
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Book updated) {
        if (!bookRepository.existsById(id)) {
            return notFound(id);
        }
        updated.setId(id);
        return ResponseEntity.ok(bookRepository.save(updated));
    }

    // DELETE /api/books/1 -> delete a book
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return notFound(id);
        }
        bookRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Book " + id + " deleted"));
    }
}