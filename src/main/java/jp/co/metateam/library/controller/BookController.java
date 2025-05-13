package jp.co.metateam.library.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {
    
    private final BookMstService bookMstService;
    private Object window;

    @Autowired
    public BookController(BookMstService bookMstService){
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();
        
        model.addAttribute("bookMstList", bookMstList);

        return "/book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "/book/add";
    }

    /**
     * @param BookMstDto
     * @param result
     * @param ra
     * @return
     */
    @PostMapping("/book/add")
    public String register(@Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, RedirectAttributes ra, Model model) {
        try{

            boolean errisbnFlg = false;
            
            if(result.hasErrors()){ 
                model.addAttribute("bookMstDto", bookMstDto);
                model.addAttribute("org.springframework.validation.BindingResult.bookMstDto", result);
                return "/book/add";
            }

            BookMst isbnExist = this.bookMstService.selectByIsbn(bookMstDto.getIsbn());

            if(isbnExist != null){
                result.rejectValue("isbn", "error.value", "ISBNは登録済みです");
                errisbnFlg = true;
                return "/book/add";
            }
            
            bookMstService.save(bookMstDto);

            return "redirect:/book/index";

        }catch(Exception e){
            log.error("登録失敗: " + e.getMessage());
            model.addAttribute("errorMessage","書籍情報の保存中にエラーが発生しました。もう一度入力をしてください。");

            return "redirect:/book/add";
        
            }
    
    }

    @GetMapping("/book/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
    Optional<BookMst> bookOpt = bookMstService.findById(id);
        if (bookOpt.isEmpty()) {
        ra.addFlashAttribute("errormessage", "該当する書籍が見つかりませんでした。");
        return "redirect:/book/index";
    }

        BookMst book = bookOpt.get();

        if (book.getDeletedAt() != null) {
        ra.addFlashAttribute("errormessage", "この書籍は削除済みのため編集できません。");
        return "redirect:/book/index";
    }

        BookMstDto dto = new BookMstDto();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());

        model.addAttribute("bookMstDto", dto);

        return "/book/edit"; // ← 作成するテンプレート名
    }

    @PostMapping("/book/edit")
    public String update(@Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, Model model, RedirectAttributes ra) {
    if (result.hasErrors()) {
        model.addAttribute("bookMstDto", bookMstDto);
        return "/book/edit";
    }

    try {
        bookMstService.update(bookMstDto);
        ra.addFlashAttribute("message", "更新完了しました");
        return "redirect:/book/index";

      } catch (IllegalArgumentException e) {
        // 変更なし or 対象が見つからない等 → メッセージ付きでindexにリダイレクト
        ra.addFlashAttribute("errormessage", e.getMessage());
        return "redirect:/book/index";

    } catch (Exception e) {
        // その他の予期しないエラー
        ra.addFlashAttribute("errormessage", "更新に失敗しました。もう一度お試しください。");
        return "redirect:/book/index";
        }
    }


    @PostMapping("/book/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
    try {
        bookMstService.deleteById(id);
        ra.addFlashAttribute("message", "削除が完了しました。");
        ra.addFlashAttribute("error", false);

    } catch (IllegalArgumentException e) {
        // 削除済みなどロジック系エラー
        ra.addFlashAttribute("errormessage", e.getMessage());
        return "redirect:/book/index";

        
    } catch (Exception e) {
        ra.addFlashAttribute("errorMessage", "削除に失敗しました。");
        ra.addFlashAttribute("error", true);
    }
        return "redirect:/book/index";


    }

}






