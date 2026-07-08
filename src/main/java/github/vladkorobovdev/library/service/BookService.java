package github.vladkorobovdev.library.service;

import java.util.List;
import java.util.Optional;

import github.vladkorobovdev.library.model.dto.BookDTO;
import github.vladkorobovdev.library.model.entity.Book;

public interface BookService {
  List<Book> findAll();

  Optional<Book> findById(Long id);

  Book create(BookDTO book);

  Optional<Book> update(Long id, BookDTO book);

  void deleteById(Long id);
}
