package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 書籍マスタ
 */
@Entity
@Table(name = "BookMst")
public class BookMst {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** ISBN */
    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    /** 書籍タイトル */
    @Column(name = "title", nullable = false)
    private String title;

    /** 削除日時 */
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    /** 削除フラグ */
    @Column(name = "delete_flag")
    private Boolean deleteFlag = Boolean.FALSE;

    // Getters
    public Long getId() {
        return this.id;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public String getTitle() {
        return this.title;
    }

    public Timestamp getDeletedAt() {
        return this.deletedAt;
    }

    public Boolean getDeleteFlag() {
        return this.deleteFlag;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setDeleteFlag(Boolean deleteFlag){
        this.deleteFlag = deleteFlag;
    }


}
