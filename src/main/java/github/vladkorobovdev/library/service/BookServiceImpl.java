package github.vladkorobovdev.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.vladkorobovdev.library.model.entity.Book;
import github.vladkorobovdev.library.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {
  private final BookRepository bookRepository;

  public BookServiceImpl(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Transactional(readOnly = true)
  public List<Book> findAll() {
    return bookRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<Book> findById(Long id) {
    return bookRepository.findById(id);
  }

  @Transactional
  public Book create(Book book) {
    return bookRepository.save(book);
  }

  @Transactional
  public Optional<Book> update(Long id, Book newBook) {
    return bookRepository.findById(id)
        .map(book -> {
          book.setAuthor(newBook.getAuthor());
          book.setTitle(newBook.getTitle());
          book.setPrice(newBook.getPrice());
          return book;
        });
  }

  @Transactional
  public void deleteById(Long id) {
    bookRepository.deleteById(id);
  }
}
