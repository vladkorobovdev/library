package github.vladkorobovdev.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import github.vladkorobovdev.library.model.dto.BookDTO;
import github.vladkorobovdev.library.model.entity.Book;
import github.vladkorobovdev.library.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

  @Mock
  private BookRepository bookRepository;
  @InjectMocks
  private BookServiceImpl bookService;

  @Test
  void create_ShouldSaveAndReturnBook() {
    BookDTO dto = new BookDTO("Title", "Author", new BigDecimal("10.00"));
    Book savedBook = Book.builder().id(1L).title("Title").author("Author").price(new BigDecimal("10.00")).build();

    when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

    Book result = bookService.create(dto);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Title", result.getTitle());
  }

  @Test
  void findAll_ShouldReturnListOfBooks() {
    when(bookRepository.findAll()).thenReturn(List.of(new Book(), new Book()));

    List<Book> result = bookService.findAll();

    assertEquals(2, result.size());
    verify(bookRepository).findAll();
  }
}
