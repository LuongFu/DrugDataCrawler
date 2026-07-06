Tool cào thông tin thuốc của Bộ Y Tế Cục Quản Lý Dược https://dichvucong.dav.gov.vn/congbothuoc/index 

cách chạy:
git clone, build và chỉnh sửa lại số lượng page size muốn cào.

*Note: Với phanLoaiThuocEnum = 3 (Vắc-xin): Trả về các tên như: Vaxigrip, Envacgen, Flumist, Arexvy. Đây đều là Vắc-xin (vắc-xin cúm, vi-rút hô hấp, vi-rút bại liệt...).

Với phanLoaiThuocEnum = 4 (Sinh phẩm y tế): Trả về các tên như: Thymoglobuline, Artlegia, Vegzelma, Truxima.  Đây là các Sinh phẩm y tế (Biologics / Sinh phẩm điều trị) chủ yếu là các kháng thể đơn dòng (như Rituximab, Bevacizumab).

Mapping bảng phân loại (phanLoaiThuocEnum) của Cục Quản lý Dược như sau:

1: Thuốc kê đơn (ETC)
2: Thuốc không kê đơn (OTC)
3: Vắc-xin
4: Sinh phẩm y tế
