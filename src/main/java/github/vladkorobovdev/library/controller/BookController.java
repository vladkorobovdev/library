package github.vladkorobovdev.library.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.vladkorobovdev.library.model.dto.BookDTO;
import github.vladkorobovdev.library.model.entity.Book;
import github.vladkorobovdev.library.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping()
  public List<Book> getAllBooks() {
    return bookService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Book> getBookById(@PathVariable Long id) {
    return ResponseEntity.of(bookService.findById(id));
  }

  @PostMapping
  public ResponseEntity<Book> createBook(@RequestBody BookDTO book) {
    Book savedBook = bookService.create(book);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Book> updateBook(
      @PathVariable Long id,
      @RequestBody BookDTO book) {
    return bookService.update(id, book)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    bookService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
