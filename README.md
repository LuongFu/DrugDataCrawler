# Drug Data Crawler (Tool Cào Dữ Liệu Thuốc)

Tool cào thông tin thuốc (dữ liệu công khai) từ trang web của Cục Quản Lý Dược - Bộ Y Tế: [https://dichvucong.dav.gov.vn/congbothuoc/index](https://dichvucong.dav.gov.vn/congbothuoc/index)

Ứng dụng được viết bằng Java Spring Boot, sử dụng kỹ thuật multi-threading (đa luồng) để tăng tốc độ cào dữ liệu và lưu trực tiếp vào cơ sở dữ liệu PostgreSQL.

## Tính năng chính
- Cào dữ liệu chi tiết của các loại thuốc (Thông tin cơ bản, Thông tin đăng ký, Hoạt chất, Công ty sản xuất, Công ty đăng ký,...).
- Tích hợp đa luồng (Multi-threading) giúp xử lý lượng dữ liệu lớn một cách nhanh chóng.
- Tự động mapping và lưu thông tin vào bảng `thuoc` trong cơ sở dữ liệu PostgreSQL.
- Bỏ qua các bản ghi trùng lặp (dựa vào `so_dang_ky`).

## Yêu cầu hệ thống
- Java 17+ (hoặc phiên bản tương thích)
- PostgreSQL
- Maven

## Cài đặt và Cách chạy

1. **Clone repository:**
   ```bash
   git clone <repo_url>
   cd crawldrugdata
   ```

2. **Cấu hình Database (PostgreSQL):**
   Mở file `src/main/resources/application.properties` và cấu hình thông tin kết nối database. (Chú ý tạo sẵn database `drfamilyoi_db` trong PostgreSQL).
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/drfamilyoi_db
   spring.datasource.username=postgres
   spring.datasource.password=postgre
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Cấu hình thông số Crawl:**
   Tùy chỉnh số lượng trang (`totalPages`) và số lượng luồng (`threadCount`) trong code khi gọi hàm `startCrawling(totalPages, threadCount)` của service `DrugCrawlerService`. (Mặc định page size là 20).

4. **Chạy ứng dụng:**
   Bạn có thể chạy trực tiếp qua IDE (Run class Main/Application) hoặc sử dụng lệnh Maven:
   ```bash
   mvn spring-boot:run
   ```

## Phân loại thuốc (phanLoaiThuocEnum)

Mapping bảng phân loại thuốc của Cục Quản lý Dược:

| ID | Tên phân loại | Ví dụ / Ghi chú |
|---|---|---|
| **1** | Thuốc kê đơn (ETC) | |
| **2** | Thuốc không kê đơn (OTC) | |
| **3** | Vắc-xin | Trả về các tên như: *Vaxigrip, Envacgen, Flumist, Arexvy*. Đây đều là Vắc-xin (vắc-xin cúm, vi-rút hô hấp, vi-rút bại liệt...). |
| **4** | Sinh phẩm y tế | Trả về các tên như: *Thymoglobuline, Artlegia, Vegzelma, Truxima*. Đây là các Sinh phẩm y tế (Biologics / Sinh phẩm điều trị) chủ yếu là các kháng thể đơn dòng (như Rituximab, Bevacizumab). |
