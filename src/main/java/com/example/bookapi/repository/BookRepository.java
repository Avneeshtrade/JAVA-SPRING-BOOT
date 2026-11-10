package com.example.bookapi.repository;

import com.example.bookapi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring generates all the basic SQL (save, findAll, findById, deleteById...) for us
public interface BookRepository extends JpaRepository<Book, Long> {
}
