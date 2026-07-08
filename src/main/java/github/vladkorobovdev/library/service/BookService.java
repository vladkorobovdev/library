package github.vladkorobovdev.library.service;

import java.util.List;
import java.util.Optional;

import github.vladkorobovdev.library.model.entity.Book;

public interface BookService {
  List<Book> findAll();

  Optional<Book> findById(Long id);

  Book create(Book book);

  Optional<Book> update(Long id, Book book);

  void deleteById(Long id);
}
