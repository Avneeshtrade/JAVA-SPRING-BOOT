package com.example.bookapi.controller;

import com.example.bookapi.model.Book;
import com.example.bookapi.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
