package jp.co.metateam.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface BookMstRepository extends JpaRepository<BookMst, Long > {

	@Query(value = "SELECT * FROM book_mst WHERE deleted_at IS NULL LIMIT 1000", nativeQuery = true)
	List<BookMst> findLimitedBook();

	@Query(value = "SELECT * FROM book_mst WHERE id = ?1 AND deleted_at IS NULL", nativeQuery = true)
	BookMst selectById(Long id);

	@Query("SELECT b FROM BookMst b WHERE b.isbn = ?1")
    BookMst selectByIsbn(String isbn);

}
