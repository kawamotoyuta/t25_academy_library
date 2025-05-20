package jp.co.metateam.library.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;
    
    @Autowired
    public BookMstService(BookMstRepository bookMstRepository){
        this.bookMstRepository = bookMstRepository;
    }

    public BookMst selectByIsbn(String isbn){
        return bookMstRepository.selectByIsbn(isbn);

    }

    public Optional<BookMst> findById(Long id) {
        return bookMstRepository.findById(id); // JpaRepositoryが提供するfindByIdを利用
    }

    @Transactional
    public void deleteById(Long id) {
    Optional<BookMst> bookOpt = bookMstRepository.findById(id);
    if (bookOpt.isEmpty()) {
        throw new IllegalArgumentException("書籍が存在しません。");
    }    
    
    BookMst book = bookOpt.get();

    if (book.getDeleteFlag() != null && book.getDeleteFlag()) {
    throw new IllegalArgumentException("この書籍は既に削除されています。");
    }
    
    // 論理削除処理
    book.setDeleteFlag(Boolean.TRUE);  // 削除されている判断（1）
    book.setDeletedAt(new Timestamp(System.currentTimeMillis()));//削除日時

    bookMstRepository.save(book);  // 更新保存
}


    @Transactional
    public void update(BookMstDto dto) {
    Optional<BookMst> bookOpt = bookMstRepository.findById(dto.getId());
    if (bookOpt.isEmpty()) {
        throw new IllegalArgumentException("対象の書籍が見つかりません。削除された可能性があります。");
        
    }

    BookMst book = bookOpt.get();

    if (book.getDeleteFlag() != null && book.getDeleteFlag()) {
    throw new IllegalArgumentException("この書籍は既に削除されています。編集できません。");
    }


    // 差分があれば更新
    book.setIsbn(dto.getIsbn());
    book.setTitle(dto.getTitle());
    bookMstRepository.save(book); 
}
    
    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }

    @Transactional
    public void save(BookMstDto bookMstDto) {
        BookMst bookMst = new BookMst();
        bookMst.setIsbn(bookMstDto.getIsbn());
        bookMst.setTitle(bookMstDto.getTitle());
        bookMst.setDeleteFlag(Boolean.FALSE); 

        this.bookMstRepository.save(bookMst);

    }

    public Object getIsbn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getIsbn'");
    }

    public Object getTitle() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTitle'");
    }

}

