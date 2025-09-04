# TravelApp

Ứng dụng quản lý du lịch trên nền tảng Java Swing.

## Tính năng chính
- Quản lý khách hàng, tour, booking, thanh toán và hóa đơn.
- Phân quyền người dùng (ADMIN, SALES, ACCOUNTANT, GUIDE, CS).
- Giao diện FlatLaf; cơ sở dữ liệu PostgreSQL quản lý bằng Flyway.

## Yêu cầu
- [JDK 17](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)

## Cài đặt & chạy
1. Clone dự án: `git clone <repo>`
2. Cấu hình kết nối DB trong `src/main/resources/application.properties`.
3. Build dự án: `mvn clean package`
4. Chạy ứng dụng:
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.travelapp.TravelApplication"
   ```
   Flyway sẽ tự động chạy migrations khi khởi động lần đầu.

## Tài khoản mặc định
```
username: admin, sales, accountant, guide, cs
password: 123
```

## Tài liệu khác
- Cấu trúc bảng và luồng nghiệp vụ: `database_dictionary.md`

## Kiểm thử
Chạy tất cả bài kiểm thử bằng lệnh:
```bash
mvn test
```

## Giấy phép
Dự án dùng cho mục đích học tập.
