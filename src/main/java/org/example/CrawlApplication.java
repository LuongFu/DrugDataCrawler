package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class CrawlApplication implements CommandLineRunner {

    @Autowired
    private DrugCrawlerService drugCrawlerService;

    public static void main(String[] args) {
        SpringApplication.run(CrawlApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=========================================");
        System.out.println("  ỨNG DỤNG SPRING BOOT ĐÃ KHỞI ĐỘNG      ");
        System.out.println("=========================================");

        // SỐ 10: Số lượng trang cần crawl (Bạn có thể đổi thành 2000 hoặc bao nhiêu tùy ý)
        // SỐ 4: Số luồng chạy song song (Khuyên dùng từ 3 - 5 luồng là an toàn)
        drugCrawlerService.startCrawling(2734, 10);

        System.out.println("Đã chạy xong kịch bản trong CommandLineRunner.");
    }
}