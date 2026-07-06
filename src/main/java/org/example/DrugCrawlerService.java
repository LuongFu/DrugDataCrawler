package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger; // THÊM DÒNG NÀY ĐỂ ĐẾM TIẾN ĐỘ

@Service
public class DrugCrawlerService {

    @Autowired
    private ThuocRepository thuocRepository;

    // Biến đếm an toàn cho đa luồng
    private final AtomicInteger completedPages = new AtomicInteger(0);

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    private final Gson gson = new Gson();
    private static final String API_URL = "https://dichvucong.dav.gov.vn/api/services/app/soDangKy/GetAllPublicServerPaging";

    public void startCrawling(int totalPages, int threadCount) {
        System.out.println("=============================================================");
        System.out.println(" Bắt đầu Crawl đa luồng với: " + threadCount + " luồng.");
        System.out.println(" Tổng số trang: " + totalPages);
        System.out.println("=============================================================");

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        int pageSize = 20;

        for (int page = 1; page <= totalPages; page++) {
            final int currentPage = page;
            executorService.submit(() -> crawlSinglePage(currentPage, pageSize, totalPages));
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                System.err.println("Quá thời gian chờ!");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("=============================================================");
        System.out.println(" HOÀN THÀNH 100%! ĐÃ CRAWL HẾT " + totalPages + " TRANG.");
        System.out.println("=============================================================");
    }

    private void crawlSinglePage(int page, int pageSize, int totalPages) {
        int skip = (page - 1) * pageSize;
        String threadName = Thread.currentThread().getName();

        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String jsonPayload = String.format("{\"SoDangKyThuoc\":{},\"KichHoat\":true,\"skipCount\":%d,\"maxResultCount\":%d,\"sorting\":null}", skip, pageSize);
            RequestBody body = RequestBody.create(jsonPayload, JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);

                    if (jsonObject.has("result")) {
                        JsonArray drugArray = jsonObject.getAsJsonObject("result").getAsJsonArray("items");
                        List<ThuocEntity> thuocList = new ArrayList<>();

                        for (JsonElement element : drugArray) {
                            JsonObject drugJson = element.getAsJsonObject();
                            String soDangKy = getStringFromJson(drugJson, "soDangKy");

                            if (soDangKy != null && !thuocRepository.existsBySoDangKy(soDangKy)) {
                                ThuocEntity thuoc = new ThuocEntity();

                                // Thông tin cơ bản
                                thuoc.setSoDangKy(soDangKy);
                                thuoc.setSoDangKyCu(getStringFromJson(drugJson, "soDangKyCu"));
                                thuoc.setTenThuoc(getStringFromJson(drugJson, "tenThuoc"));
                                thuoc.setPhanLoaiThuocEnum(getIntFromJson(drugJson, "phanLoaiThuocEnum"));
                                thuoc.setIsHetHan(getBoolFromJson(drugJson, "isHetHan"));
                                thuoc.setIsDuocPhep(getBoolFromJson(drugJson, "isDuocPhep"));
                                thuoc.setIsDaRutSoDangKy(getBoolFromJson(drugJson, "isDaRutSoDangKy"));
                                thuoc.setIsActive(getBoolFromJson(drugJson, "isActive"));
                                thuoc.setMaSoHoSoGiaHan(getStringFromJson(drugJson, "maSoHoSoGiaHan"));
                                thuoc.setNgayTiepNhanHSGiaHan(getStringFromJson(drugJson, "ngayTiepNhanHSGiaHan"));
                                thuoc.setGhiChu(getStringFromJson(drugJson, "ghiChu"));

                                // Thông tin đăng ký thuốc
                                if (drugJson.has("thongTinDangKyThuoc") && !drugJson.get("thongTinDangKyThuoc").isJsonNull()) {
                                    JsonObject dkThuoc = drugJson.getAsJsonObject("thongTinDangKyThuoc");
                                    thuoc.setNgayCapSoDangKy(getStringFromJson(dkThuoc, "ngayCapSoDangKy"));
                                    thuoc.setNgayGiaHanSoDangKy(getStringFromJson(dkThuoc, "ngayGiaHanSoDangKy"));
                                    thuoc.setNgayHetHanSoDangKy(getStringFromJson(dkThuoc, "ngayHetHanSoDangKy"));
                                    thuoc.setSoQuyetDinh(getStringFromJson(dkThuoc, "soQuyetDinh"));
                                    thuoc.setDotCap(getStringFromJson(dkThuoc, "dotCap"));
                                }

                                // Thông tin thuốc cơ bản (hoạt chất, hàm lượng, dạng bào chế, ...)
                                if (drugJson.has("thongTinThuocCoBan") && !drugJson.get("thongTinThuocCoBan").isJsonNull()) {
                                    JsonObject coBan = drugJson.getAsJsonObject("thongTinThuocCoBan");
                                    thuoc.setHoatChatChinh(getStringFromJson(coBan, "hoatChatChinh"));
                                    thuoc.setHamLuong(getStringFromJson(coBan, "hamLuong"));
                                    thuoc.setDangBaoChe(getStringFromJson(coBan, "dangBaoChe"));
                                    thuoc.setDongGoi(getStringFromJson(coBan, "dongGoi"));
                                    thuoc.setTieuChuan(getStringFromJson(coBan, "tieuChuan"));
                                    thuoc.setTuoiTho(getStringFromJson(coBan, "tuoiTho"));
                                    thuoc.setDuongDung(getStringFromJson(coBan, "tenDuongDung"));
                                }

                                // Công ty sản xuất
                                if (drugJson.has("congTySanXuat") && !drugJson.get("congTySanXuat").isJsonNull()) {
                                    JsonObject sx = drugJson.getAsJsonObject("congTySanXuat");
                                    thuoc.setCongTySanXuat(getStringFromJson(sx, "tenCongTySanXuat"));
                                    thuoc.setDiaChiSanXuat(getStringFromJson(sx, "diaChiSanXuat"));
                                    thuoc.setNuocSanXuat(getStringFromJson(sx, "nuocSanXuat"));
                                }

                                // Công ty đăng ký
                                if (drugJson.has("congTyDangKy") && !drugJson.get("congTyDangKy").isJsonNull()) {
                                    JsonObject dk = drugJson.getAsJsonObject("congTyDangKy");
                                    thuoc.setCongTyDangKy(getStringFromJson(dk, "tenCongTyDangKy"));
                                    thuoc.setDiaChiDangKy(getStringFromJson(dk, "diaChiDangKy"));
                                    thuoc.setNuocDangKy(getStringFromJson(dk, "nuocDangKy"));
                                }

                                thuocList.add(thuoc);
                            }
                        }

                        if (!thuocList.isEmpty()) {
                            try {
                                thuocRepository.saveAll(thuocList);
                            } catch (DataIntegrityViolationException ignored) {}
                        }
                    }
                }
            }

            // Cập nhật tiến độ
            int count = completedPages.incrementAndGet();
            double percent = ((double) count / totalPages) * 100;
            System.out.printf("[%s] Xong trang %d/%d (%.2f%%)%n", threadName, count, totalPages, percent);

            Thread.sleep(1000); // Giãn cách 1 giây để tránh bị chặn
        } catch (Exception e) {
            System.err.println("Lỗi trang " + page + ": " + e.getMessage());
        }
    }

    private String getStringFromJson(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }

    private Integer getIntFromJson(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsInt();
        }
        return null;
    }

    private Boolean getBoolFromJson(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsBoolean();
        }
        return null;
    }
}