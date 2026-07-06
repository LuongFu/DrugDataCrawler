package org.example;

import okhttp3.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class DrugCrawlerService {

    @Autowired
    private ThuocRepository thuocRepository; // Tiêm Repository vào đây

    // Khởi tạo OkHttpClient với cấu hình timeout
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    private final Gson gson = new Gson();

    // URL API chính thức của Cục Quản lý Dược
    private static final String API_URL = "https://dichvucong.dav.gov.vn/api/services/app/soDangKy/GetAllPublicServerPaging";

    /**
     * Hàm crawl dữ liệu thuốc ĐA LUỒNG
     * @param totalPages Tổng số trang cần lấy
     * @param threadCount Số luồng chạy song song (Khuyên dùng từ 3-5 luồng)
     */
    public void startCrawling(int totalPages, int threadCount) {
        System.out.println("=============================================================");
        System.out.println(" Bắt đầu Crawl đa luồng với: " + threadCount + " luồng song song.");
        System.out.println("=============================================================");

        // Khởi tạo Thread Pool với số luồng cấu hình
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        int pageSize = 20; // Số bản ghi trên 1 trang

        for (int page = 1; page <= totalPages; page++) {
            final int currentPage = page;

            // Giao việc cho từng luồng trong Thread Pool xử lý
            executorService.submit(() -> {
                crawlSinglePage(currentPage, pageSize);
            });
        }

        // Sau khi đã giao hết việc, ra lệnh cho Thread Pool đóng cửa (không nhận việc mới nữa)
        executorService.shutdown();

        try {
            // Chờ đợi cho đến khi TẤT CẢ các luồng hoàn thành xong công việc của mình
            // Đặt thời gian chờ tối đa là 1 tiếng (hoặc tùy bạn cấu hình)
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                System.err.println("Quá thời gian chờ, một số luồng chưa chạy xong!");
            }
        } catch (InterruptedException e) {
            System.err.println("Tiến trình chính bị gián đoạn khi đang chờ các luồng con.");
            Thread.currentThread().interrupt();
        }

        System.out.println("=============================================================");
        System.out.println(" ĐÃ HOÀN THÀNH TOÀN BỘ TIẾN TRÌNH CRAWL ĐA LUỒNG!");
        System.out.println("=============================================================");
    }

    /**
     * Hàm phụ trách xử lý việc crawl cho duy nhất một trang
     */
    private void crawlSinglePage(int page, int pageSize) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Đang xử lý trang: " + page);

        int skip = (page - 1) * pageSize;

        try {
            // 1. Khai báo kiểu dữ liệu JSON cho Body
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            // 2. Tạo JSON Payload gửi đi
            String jsonPayload = String.format("{\"SoDangKyThuoc\":{},\"KichHoat\":true,\"skipCount\":%d,\"maxResultCount\":%d,\"sorting\":null}", skip, pageSize);
            RequestBody body = RequestBody.create(jsonPayload, JSON);

            // 3. Tạo Request POST kèm Headers giả lập trình duyệt
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                    .addHeader("Origin", "https://dichvucong.dav.gov.vn")
                    .addHeader("Referer", "https://dichvucong.dav.gov.vn/congbothuoc/index")
                    .build();

            // 4. Thực thi Request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();

                    // 5. Phân tích cú pháp JSON trả về
                    JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
                    JsonArray drugArray = null;

                    if (jsonObject.has("result")) {
                        JsonObject resultObj = jsonObject.getAsJsonObject("result");
                        if (resultObj.has("items")) {
                            drugArray = resultObj.getAsJsonArray("items");
                        }
                    }

                    if (drugArray != null && drugArray.size() > 0) {
                        List<ThuocEntity> thuocList = new ArrayList<>();

                        // Duyệt qua từng object JSON trong mảng
                        for (int i = 0; i < drugArray.size(); i++) {
                            JsonObject drugJson = drugArray.get(i).getAsJsonObject();

                            String soDangKy = getStringFromJson(drugJson, "soDangKy");
                            String tenThuoc = getStringFromJson(drugJson, "tenThuoc");
                            String hoatChat = getStringFromJson(drugJson, "hoatChatChinh");
                            String congTy = getStringFromJson(drugJson, "tenCongTySanXuat");

                            // Đồng bộ hóa (synchronized) việc kiểm tra và lưu DB để tránh 2 luồng cùng lúc kiểm tra trùng lặp bị lỗi
                            synchronized (this) {
                                if (soDangKy != null && !thuocRepository.existsBySoDangKy(soDangKy)) {
                                    ThuocEntity thuoc = new ThuocEntity();
                                    thuoc.setSoDangKy(soDangKy);
                                    thuoc.setTenThuoc(tenThuoc);
                                    thuoc.setHoatChatChinh(hoatChat);
                                    thuoc.setCongTySanXuat(congTy);

                                    thuocList.add(thuoc);
                                }
                            }
                        }

                        // Lưu danh sách vào PostgreSQL
                        if (!thuocList.isEmpty()) {
                            thuocRepository.saveAll(thuocList);
                            System.out.println("  -> [" + threadName + "] Đã lưu mới " + thuocList.size() + " bản ghi từ trang " + page + " vào DB.");
                        } else {
                            System.out.println("  -> [" + threadName + "] Trang " + page + " không có bản ghi mới để lưu.");
                        }

                    } else {
                        System.out.println("  -> [" + threadName + "] Trang " + page + " trống hoặc sai cấu trúc.");
                    }
                } else {
                    System.err.println("  -> [" + threadName + "] Lỗi HTTP: " + response.code() + " ở trang " + page);
                }
            }

            // 6. GIÃN CÁCH AN TOÀN: Bắt buộc dừng lại một chút để tránh bị quét IP
            Thread.sleep(1500);

        } catch (IOException | InterruptedException e) {
            System.err.println("Lỗi khi xử lý trang " + page + ": " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Hàm tiện ích để lấy chuỗi an toàn từ JSON (tránh lỗi NullPointerException)
    private String getStringFromJson(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }
}