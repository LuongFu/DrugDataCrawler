package org.example;

import jakarta.persistence.*;

// Đánh dấu đây là một thực thể sẽ được map thành bảng trong DB
@Entity
@Table(name = "thuoc")
public class ThuocEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa chính tự tăng

    @Column(name = "so_dang_ky", unique = true)
    private String soDangKy;

    @Column(name = "ten_thuoc", length = 500)
    private String tenThuoc;

    @Column(name = "hoat_chat_chinh", length = 1000)
    private String hoatChatChinh;

    @Column(name = "cong_ty_san_xuat", length = 500)
    private String congTySanXuat;

    // --- Constructor rỗng bắt buộc của JPA ---
    public ThuocEntity() {
    }

    // --- Getters và Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSoDangKy() { return soDangKy; }
    public void setSoDangKy(String soDangKy) { this.soDangKy = soDangKy; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public String getHoatChatChinh() { return hoatChatChinh; }
    public void setHoatChatChinh(String hoatChatChinh) { this.hoatChatChinh = hoatChatChinh; }

    public String getCongTySanXuat() { return congTySanXuat; }
    public void setCongTySanXuat(String congTySanXuat) { this.congTySanXuat = congTySanXuat; }
}