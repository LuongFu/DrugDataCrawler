package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Kế thừa JpaRepository, truyền vào Tên Entity và Kiểu dữ liệu của khóa chính (Long)
@Repository
public interface ThuocRepository extends JpaRepository<ThuocEntity, Long> {

    // Spring Data JPA sẽ tự động sinh ra câu lệnh SQL tìm thuốc theo Số đăng ký
    boolean existsBySoDangKy(String soDangKy);
}