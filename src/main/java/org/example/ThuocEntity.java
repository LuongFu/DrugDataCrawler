package org.example;

import jakarta.persistence.*;

// Đánh dấu đây là một thực thể sẽ được map thành bảng trong DB
@Entity
@Table(name = "thuoc")
public class ThuocEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khóa chính tự tăng

    // ===== THÔNG TIN CƠ BẢN =====
    @Column(name = "so_dang_ky", unique = true)
    private String soDangKy;

    @Column(name = "so_dang_ky_cu")
    private String soDangKyCu;

    @Column(name = "ten_thuoc", length = 500)
    private String tenThuoc;

    @Column(name = "phan_loai_thuoc_enum")
    private Integer phanLoaiThuocEnum;

    // ===== THÔNG TIN ĐĂNG KÝ THUỐC =====
    @Column(name = "ngay_cap_so_dang_ky")
    private String ngayCapSoDangKy;

    @Column(name = "ngay_gia_han_so_dang_ky")
    private String ngayGiaHanSoDangKy;

    @Column(name = "ngay_het_han_so_dang_ky")
    private String ngayHetHanSoDangKy;

    @Column(name = "so_quyet_dinh", length = 500)
    private String soQuyetDinh;

    @Column(name = "dot_cap")
    private String dotCap;

    // ===== THÔNG TIN THUỐC CƠ BẢN =====
    @Column(name = "hoat_chat_chinh", length = 2000)
    private String hoatChatChinh;

    @Column(name = "ham_luong", length = 1000)
    private String hamLuong;

    @Column(name = "dang_bao_che", length = 500)
    private String dangBaoChe;

    @Column(name = "dong_goi", length = 2000)
    private String dongGoi;

    @Column(name = "tieu_chuan", length = 500)
    private String tieuChuan;

    @Column(name = "tuoi_tho", length = 200)
    private String tuoiTho;

    @Column(name = "duong_dung", length = 500)
    private String duongDung;

    // ===== CÔNG TY SẢN XUẤT =====
    @Column(name = "cong_ty_san_xuat", length = 500)
    private String congTySanXuat;

    @Column(name = "dia_chi_san_xuat", length = 1000)
    private String diaChiSanXuat;

    @Column(name = "nuoc_san_xuat", length = 200)
    private String nuocSanXuat;

    // ===== CÔNG TY ĐĂNG KÝ =====
    @Column(name = "cong_ty_dang_ky", length = 500)
    private String congTyDangKy;

    @Column(name = "dia_chi_dang_ky", length = 1000)
    private String diaChiDangKy;

    @Column(name = "nuoc_dang_ky", length = 200)
    private String nuocDangKy;

    // ===== TRẠNG THÁI =====
    @Column(name = "is_het_han")
    private Boolean isHetHan;

    @Column(name = "is_duoc_phep")
    private Boolean isDuocPhep;

    @Column(name = "is_da_rut_so_dang_ky")
    private Boolean isDaRutSoDangKy;

    @Column(name = "is_active")
    private Boolean isActive;

    // ===== GIA HẠN =====
    @Column(name = "ma_so_ho_so_gia_han", length = 200)
    private String maSoHoSoGiaHan;

    @Column(name = "ngay_tiep_nhan_hs_gia_han")
    private String ngayTiepNhanHSGiaHan;

    // ===== GHI CHÚ =====
    @Column(name = "ghi_chu", length = 2000)
    private String ghiChu;

    // --- Constructor rỗng bắt buộc của JPA ---
    public ThuocEntity() {
    }

    // --- Getters và Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSoDangKy() { return soDangKy; }
    public void setSoDangKy(String soDangKy) { this.soDangKy = soDangKy; }

    public String getSoDangKyCu() { return soDangKyCu; }
    public void setSoDangKyCu(String soDangKyCu) { this.soDangKyCu = soDangKyCu; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public Integer getPhanLoaiThuocEnum() { return phanLoaiThuocEnum; }
    public void setPhanLoaiThuocEnum(Integer phanLoaiThuocEnum) { this.phanLoaiThuocEnum = phanLoaiThuocEnum; }

    public String getNgayCapSoDangKy() { return ngayCapSoDangKy; }
    public void setNgayCapSoDangKy(String ngayCapSoDangKy) { this.ngayCapSoDangKy = ngayCapSoDangKy; }

    public String getNgayGiaHanSoDangKy() { return ngayGiaHanSoDangKy; }
    public void setNgayGiaHanSoDangKy(String ngayGiaHanSoDangKy) { this.ngayGiaHanSoDangKy = ngayGiaHanSoDangKy; }

    public String getNgayHetHanSoDangKy() { return ngayHetHanSoDangKy; }
    public void setNgayHetHanSoDangKy(String ngayHetHanSoDangKy) { this.ngayHetHanSoDangKy = ngayHetHanSoDangKy; }

    public String getSoQuyetDinh() { return soQuyetDinh; }
    public void setSoQuyetDinh(String soQuyetDinh) { this.soQuyetDinh = soQuyetDinh; }

    public String getDotCap() { return dotCap; }
    public void setDotCap(String dotCap) { this.dotCap = dotCap; }

    public String getHoatChatChinh() { return hoatChatChinh; }
    public void setHoatChatChinh(String hoatChatChinh) { this.hoatChatChinh = hoatChatChinh; }

    public String getHamLuong() { return hamLuong; }
    public void setHamLuong(String hamLuong) { this.hamLuong = hamLuong; }

    public String getDangBaoChe() { return dangBaoChe; }
    public void setDangBaoChe(String dangBaoChe) { this.dangBaoChe = dangBaoChe; }

    public String getDongGoi() { return dongGoi; }
    public void setDongGoi(String dongGoi) { this.dongGoi = dongGoi; }

    public String getTieuChuan() { return tieuChuan; }
    public void setTieuChuan(String tieuChuan) { this.tieuChuan = tieuChuan; }

    public String getTuoiTho() { return tuoiTho; }
    public void setTuoiTho(String tuoiTho) { this.tuoiTho = tuoiTho; }

    public String getDuongDung() { return duongDung; }
    public void setDuongDung(String duongDung) { this.duongDung = duongDung; }

    public String getCongTySanXuat() { return congTySanXuat; }
    public void setCongTySanXuat(String congTySanXuat) { this.congTySanXuat = congTySanXuat; }

    public String getDiaChiSanXuat() { return diaChiSanXuat; }
    public void setDiaChiSanXuat(String diaChiSanXuat) { this.diaChiSanXuat = diaChiSanXuat; }

    public String getNuocSanXuat() { return nuocSanXuat; }
    public void setNuocSanXuat(String nuocSanXuat) { this.nuocSanXuat = nuocSanXuat; }

    public String getCongTyDangKy() { return congTyDangKy; }
    public void setCongTyDangKy(String congTyDangKy) { this.congTyDangKy = congTyDangKy; }

    public String getDiaChiDangKy() { return diaChiDangKy; }
    public void setDiaChiDangKy(String diaChiDangKy) { this.diaChiDangKy = diaChiDangKy; }

    public String getNuocDangKy() { return nuocDangKy; }
    public void setNuocDangKy(String nuocDangKy) { this.nuocDangKy = nuocDangKy; }

    public Boolean getIsHetHan() { return isHetHan; }
    public void setIsHetHan(Boolean isHetHan) { this.isHetHan = isHetHan; }

    public Boolean getIsDuocPhep() { return isDuocPhep; }
    public void setIsDuocPhep(Boolean isDuocPhep) { this.isDuocPhep = isDuocPhep; }

    public Boolean getIsDaRutSoDangKy() { return isDaRutSoDangKy; }
    public void setIsDaRutSoDangKy(Boolean isDaRutSoDangKy) { this.isDaRutSoDangKy = isDaRutSoDangKy; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getMaSoHoSoGiaHan() { return maSoHoSoGiaHan; }
    public void setMaSoHoSoGiaHan(String maSoHoSoGiaHan) { this.maSoHoSoGiaHan = maSoHoSoGiaHan; }

    public String getNgayTiepNhanHSGiaHan() { return ngayTiepNhanHSGiaHan; }
    public void setNgayTiepNhanHSGiaHan(String ngayTiepNhanHSGiaHan) { this.ngayTiepNhanHSGiaHan = ngayTiepNhanHSGiaHan; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}