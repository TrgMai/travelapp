# Database Dictionary & Business Flows

## 1. Bảng dữ liệu (English – Tiếng Việt – Vai trò)

### 1.1. `users` (Người dùng)
- id – Mã người dùng
- username – Tên đăng nhập
- password_hash – Mật khẩu (đã mã hoá)
- full_name – Họ tên
- email – Email
- phone – Số điện thoại
- status – Trạng thái (ACTIVE/INACTIVE)
- created_at – Ngày tạo

---

### 1.2. `roles` (Vai trò)
- id – Mã vai trò
- code – Mã code vai trò (ADMIN/SALES/ACCOUNTANT/...)
- name – Tên vai trò

### 1.3. `permissions` (Quyền)
- id – Mã quyền
- code – Mã code quyền
- name – Tên quyền

### 1.4. `role_permissions` (Phân quyền cho vai trò)
- role_id – Vai trò
- permission_id – Quyền

### 1.5. `user_roles` (Gán vai trò cho user)
- user_id – Người dùng
- role_id – Vai trò

---

### 1.6. `audit_logs` (Nhật ký hệ thống)
- id – Mã log
- user_id – Người thực hiện
- action – Hành động (CREATE/UPDATE/DELETE/...)
- entity – Đối tượng (bảng nào)
- entity_id – Khoá của đối tượng
- at – Thời gian
- meta – Thông tin chi tiết (JSON)

---

### 1.7. `customers` (Khách hàng)
- id – Mã khách hàng
- full_name – Họ tên
- dob – Ngày sinh
- gender – Giới tính
- id_type – Loại giấy tờ (CCCD/Hộ chiếu)
- id_no – Số giấy tờ
- phone – Số điện thoại
- email – Email
- note – Ghi chú
- created_at – Ngày tạo

---

### 1.8. `tours` (Tour du lịch)
- id – Mã tour
- code – Mã code tour
- name – Tên tour
- route – Tuyến hành trình
- days – Số ngày
- base_price – Giá cơ bản
- description – Mô tả
- cover_image_url – Ảnh bìa
- created_at – Ngày tạo

---

### 1.9. `itineraries` (Lịch trình chi tiết tour)
- id – Mã lịch trình
- tour_id – Thuộc tour nào
- day_no – Ngày số mấy
- title – Tiêu đề
- place – Địa điểm
- activity – Hoạt động
- note – Ghi chú

---

### 1.10. `bookings` (Đơn đặt tour)
- id – Mã booking
- code – Mã code booking
- tour_id – Tour đã chọn
- status – Trạng thái (REQUESTED/CONFIRMED/COMPLETED/CANCELED)
- total_price – Tổng giá trị
- note – Ghi chú
- created_at – Ngày tạo
- updated_at – Ngày cập nhật

---

### 1.11. `booking_customers` (Khách hàng trong booking)
- booking_id – Booking
- customer_id – Khách hàng
- role – Vai trò (Trưởng đoàn, Người đi kèm…)

---

### 1.12. `payments` (Thanh toán)
- id – Mã thanh toán
- booking_id – Thuộc booking
- type – Loại thanh toán (CASH/TRANSFER/CARD)
- amount – Số tiền
- paid_at – Thời gian thanh toán
- note – Ghi chú

---

### 1.13. `invoices` (Hóa đơn)
- id – Mã hóa đơn
- booking_id – Thuộc booking
- no – Số hóa đơn
- amount – Thành tiền
- vat – Thuế VAT
- issued_at – Ngày phát hành
- pdf_path – Đường dẫn file PDF

---

### 1.14. `expenses` (Chi phí phát sinh)
- id – Mã chi phí
- booking_id – Liên kết booking
- guide_id – Hướng dẫn viên thực hiện
- amount – Số tiền
- category – Loại chi phí (Ăn uống, Vé tham quan…)
- note – Ghi chú
- spent_at – Ngày chi

---

### 1.15. `partners` (Đối tác)
- id – Mã đối tác
- type – Loại đối tác (Khách sạn, Vận chuyển, Hướng dẫn viên…)
- name – Tên đối tác
- contact – Người liên hệ
- phone – Điện thoại
- email – Email
- tax_no – Mã số thuế
- address – Địa chỉ
- note – Ghi chú

---

### 1.16. `services` (Dịch vụ cung cấp bởi đối tác)
- id – Mã dịch vụ
- partner_id – Thuộc đối tác nào
- type – Loại dịch vụ
- name – Tên dịch vụ
- unit_price – Đơn giá
- capacity – Sức chứa
- note – Ghi chú

---

### 1.17. `allocations` (Phân bổ dịch vụ vào booking)
- id – Mã phân bổ
- booking_id – Booking
- day_no – Ngày số mấy
- service_id – Dịch vụ được phân bổ
- detail – Chi tiết (JSON)

---

### 1.18. `payables` (Khoản phải trả đối tác)
- id – Mã payable
- partner_id – Đối tác
- booking_id – Liên kết booking
- amount – Số tiền phải trả
- due_date – Hạn thanh toán
- status – Trạng thái (PENDING/APPROVED/PAID)

---

### 1.19. `id_sequences` (Sinh mã tự động)
- prefix – Tiền tố (BK, C, T…)
- next_num – Số tiếp theo

---

### 1.20. `flyway_schema_history` (Lịch sử migration DB)
- Dùng cho Flyway quản lý phiên bản CSDL.

---

## 2. Luồng dữ liệu giữa các bảng

1. **Quản lý khách hàng & booking**  
   - `customers` → `booking_customers` → `bookings` → `tours` → `itineraries`
2. **Tài chính booking**  
   - `bookings` → `payments`, `invoices`, `expenses`, `payables`
3. **Đối tác & dịch vụ**  
   - `partners` → `services` → `allocations` → `bookings`
   - `partners` → `payables`
4. **Phân quyền & bảo mật**  
   - `users` → `user_roles` → `roles` → `role_permissions` → `permissions`
   - `audit_logs` ghi nhận toàn bộ hành động.

---

## 3. Luồng nghiệp vụ theo vai trò

### 3.1. ADMIN
- Quản lý toàn bộ hệ thống.
- Tạo người dùng, gán vai trò (`users`, `roles`, `permissions`).
- Xem log (`audit_logs`).
- Khởi tạo dữ liệu gốc (`id_sequences`, `partners`, `services`, `tours`).

### 3.2. SALES (Nhân viên kinh doanh)
- Tạo & quản lý khách hàng (`customers`).
- Tạo booking, thêm khách vào booking (`bookings`, `booking_customers`).
- Gán dịch vụ vào booking (`allocations`).
- Theo dõi trạng thái booking.

### 3.3. ACCOUNTANT (Kế toán)
- Quản lý thanh toán khách hàng (`payments`).
- Xuất & lưu hóa đơn (`invoices`).
- Ghi nhận chi phí (`expenses`).
- Theo dõi công nợ phải trả đối tác (`payables`).

### 3.4. GUIDE (Hướng dẫn viên)
- Được gán vào booking thông qua `expenses.guide_id`.
- Ghi nhận chi phí thực tế trong tour (`expenses`).
- Theo dõi lịch trình (`itineraries`).

### 3.5. CS (Chăm sóc khách hàng)
- Quản lý hồ sơ khách (`customers`).
- Theo dõi phản hồi/ghi chú booking (`bookings.note`).
- Hỗ trợ cập nhật tình trạng khách hàng.

---