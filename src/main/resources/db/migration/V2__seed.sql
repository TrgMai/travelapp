-- V2__seed.sql (REWRITE, realistic, 100 rows/table)
-- Compatible with V1__init.sql. Idempotent via ON CONFLICT.

-- ============================================================
-- 0) ROLES
INSERT INTO roles (id, code, name)
VALUES
  (next_id('R'), 'ADMIN', 'Administrator'),
  (next_id('R'), 'SALES', 'Sales'),
  (next_id('R'), 'ACCOUNTANT', 'Accountant'),
  (next_id('R'), 'GUIDE', 'Guide'),
  (next_id('R'), 'CS', 'Customer Service')
ON CONFLICT (code) DO NOTHING;

-- ============================================================
-- 1) PERMISSIONS (Expanded, include legacy referenced)
INSERT INTO permissions (id, code, name) VALUES
  -- User/Role/Perm
  (next_id('PR'),'USER_VIEW','View users'),
  (next_id('PR'),'USER_MANAGE','Manage users'),
  (next_id('PR'),'ROLE_VIEW','View roles'),
  (next_id('PR'),'ROLE_MANAGE','Manage roles'),
  (next_id('PR'),'PERM_VIEW','View permissions'),
  (next_id('PR'),'PERM_MANAGE','Manage permissions'),

  -- Customers
  (next_id('PR'),'CUSTOMER_CREATE','Create customer'),
  (next_id('PR'),'CUSTOMER_EDIT','Edit customer'),
  (next_id('PR'),'CUSTOMER_DELETE','Delete customer'),
  (next_id('PR'),'CUSTOMER_VIEW','View customer'),

  -- Partners
  (next_id('PR'),'PARTNER_CREATE','Create partner'),
  (next_id('PR'),'PARTNER_EDIT','Edit partner'),
  (next_id('PR'),'PARTNER_DELETE','Delete partner'),
  (next_id('PR'),'PARTNER_VIEW','View partner'),

  -- Services
  (next_id('PR'),'SERVICE_CREATE','Create service'),
  (next_id('PR'),'SERVICE_EDIT','Edit service'),
  (next_id('PR'),'SERVICE_DELETE','Delete service'),
  (next_id('PR'),'SERVICE_VIEW','View service'),

  -- Tours
  (next_id('PR'),'TOUR_CREATE','Create tour'),
  (next_id('PR'),'TOUR_EDIT','Edit tour'),
  (next_id('PR'),'TOUR_DELETE','Delete tour'),
  (next_id('PR'),'TOUR_VIEW','View tour'),

  -- Itineraries
  (next_id('PR'),'ITINERARY_CREATE','Create itinerary'),
  (next_id('PR'),'ITINERARY_EDIT','Edit itinerary'),
  (next_id('PR'),'ITINERARY_DELETE','Delete itinerary'),
  (next_id('PR'),'ITINERARY_VIEW','View itinerary'),

  -- Bookings
  (next_id('PR'),'BOOKING_CREATE','Create booking'),
  (next_id('PR'),'BOOKING_EDIT','Edit booking'),
  (next_id('PR'),'BOOKING_DELETE','Delete booking'),
  (next_id('PR'),'BOOKING_CANCEL','Cancel booking'),
  (next_id('PR'),'BOOKING_VIEW','View booking'),

  -- Allocations
  (next_id('PR'),'ALLOCATION_CREATE','Create allocation'),
  (next_id('PR'),'ALLOCATION_EDIT','Edit allocation'),
  (next_id('PR'),'ALLOCATION_DELETE','Delete allocation'),
  (next_id('PR'),'ALLOCATION_VIEW','View allocation'),
  (next_id('PR'),'ALLOCATE_RESOURCES','Allocate resources (legacy)'),

  -- Payments
  (next_id('PR'),'PAYMENT_CREATE','Create payment'),
  (next_id('PR'),'PAYMENT_EDIT','Edit payment'),
  (next_id('PR'),'PAYMENT_DELETE','Delete payment'),
  (next_id('PR'),'PAYMENT_VIEW','View payment'),
  (next_id('PR'),'PAYMENT_RECORD','Record payment (legacy)'),

  -- Invoices
  (next_id('PR'),'INVOICE_CREATE','Create invoice'),
  (next_id('PR'),'INVOICE_EDIT','Edit invoice'),
  (next_id('PR'),'INVOICE_DELETE','Delete invoice'),
  (next_id('PR'),'INVOICE_VIEW','View invoice'),
  (next_id('PR'),'INVOICE_ISSUE','Issue invoice (legacy)'),

  -- Payables / AR/AP
  (next_id('PR'),'PAYABLE_CREATE','Create payable'),
  (next_id('PR'),'PAYABLE_EDIT','Edit payable'),
  (next_id('PR'),'PAYABLE_DELETE','Delete payable'),
  (next_id('PR'),'PAYABLE_VIEW','View payable'),
  (next_id('PR'),'AP_VIEW','View accounts payable (legacy)'),
  (next_id('PR'),'AR_VIEW','View accounts receivable (legacy)'),

  -- Expenses
  (next_id('PR'),'EXPENSE_CREATE','Create expense'),
  (next_id('PR'),'EXPENSE_EDIT','Edit expense'),
  (next_id('PR'),'EXPENSE_DELETE','Delete expense'),
  (next_id('PR'),'EXPENSE_VIEW','View expense'),
  (next_id('PR'),'EXPENSE_SUBMIT','Submit expense (legacy)'),
  (next_id('PR'),'EXPENSE_APPROVE','Approve expense'),

  -- Reports, Audit, System + extras referenced
  (next_id('PR'),'REPORT_FINANCE_VIEW','View finance report'),
  (next_id('PR'),'REPORT_OPERATION_VIEW','View operation report'),
  (next_id('PR'),'AUDIT_VIEW','View audit log'),
  (next_id('PR'),'AUDIT_EXPORT','Export audit log'),
  (next_id('PR'),'AUDIT_MANAGE','Manage audit policy'),
  (next_id('PR'),'SYSTEM_CONFIG','System configuration'),
  (next_id('PR'),'CLOSE_PERIOD','Close accounting period'),
  (next_id('PR'),'INCIDENT_REPORT','Create incident report')
ON CONFLICT (code) DO NOTHING;

-- ============================================================
-- 2) ROLE-PERMISSION MAPPING
-- ADMIN: full
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r CROSS JOIN permissions p
WHERE r.code = 'ADMIN'
ON CONFLICT DO NOTHING;

-- SALES: bán tour, vận hành
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'SALES' AND p.code IN (
  'CUSTOMER_CREATE','CUSTOMER_EDIT','CUSTOMER_VIEW',
  'PARTNER_VIEW','SERVICE_VIEW',
  'TOUR_CREATE','TOUR_EDIT','TOUR_VIEW',
  'ITINERARY_EDIT','ITINERARY_VIEW',
  'BOOKING_CREATE','BOOKING_EDIT','BOOKING_CANCEL','BOOKING_VIEW',
  'ALLOCATION_VIEW','ALLOCATE_RESOURCES',
  'REPORT_OPERATION_VIEW','AUDIT_VIEW'
)
ON CONFLICT DO NOTHING;

-- ACCOUNTANT: tài chính/kế toán
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'ACCOUNTANT' AND p.code IN (
  'PAYMENT_CREATE','PAYMENT_EDIT','PAYMENT_VIEW','PAYMENT_RECORD',
  'INVOICE_CREATE','INVOICE_EDIT','INVOICE_VIEW','INVOICE_ISSUE',
  'PAYABLE_CREATE','PAYABLE_EDIT','PAYABLE_VIEW',
  'AP_VIEW','AR_VIEW','REPORT_FINANCE_VIEW','CLOSE_PERIOD',
  'BOOKING_VIEW','TOUR_VIEW','ITINERARY_VIEW'
)
ON CONFLICT DO NOTHING;

-- GUIDE: hướng dẫn viên
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'GUIDE' AND p.code IN (
  'ITINERARY_VIEW','BOOKING_VIEW','EXPENSE_CREATE','EXPENSE_VIEW','EXPENSE_SUBMIT','INCIDENT_REPORT'
)
ON CONFLICT DO NOTHING;

-- CS: chăm sóc khách hàng
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'CS' AND p.code IN (
  'CUSTOMER_VIEW','BOOKING_VIEW','TOUR_VIEW','ITINERARY_VIEW','REPORT_OPERATION_VIEW'
)
ON CONFLICT DO NOTHING;

-- ============================================================
-- 3) USERS (BCrypt "123456")
INSERT INTO users (id, username, password_hash, full_name, email, phone, status)
VALUES
  (next_id('U'),'admin',      '$2a$10$s5iUlpaTU8C4/AGkgEvXaeAJX2b1vnZzG/RxixEmmLP0kwbQBRjTK','Administrator','admin@example.com','0900000001','ACTIVE'),
  (next_id('U'),'sales',      '$2a$10$s5iUlpaTU8C4/AGkgEvXaeAJX2b1vnZzG/RxixEmmLP0kwbQBRjTK','Sales User','sales@example.com','0900000002','ACTIVE'),
  (next_id('U'),'accountant', '$2a$10$s5iUlpaTU8C4/AGkgEvXaeAJX2b1vnZzG/RxixEmmLP0kwbQBRjTK','Accountant User','acc@example.com','0900000003','ACTIVE'),
  (next_id('U'),'guide',      '$2a$10$s5iUlpaTU8C4/AGkgEvXaeAJX2b1vnZzG/RxixEmmLP0kwbQBRjTK','Guide User','guide@example.com','0900000004','ACTIVE'),
  (next_id('U'),'cs',         '$2a$10$s5iUlpaTU8C4/AGkgEvXaeAJX2b1vnZzG/RxixEmmLP0kwbQBRjTK','Customer Service','cs@example.com','0900000005','ACTIVE')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON u.username='admin' AND r.code='ADMIN' ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON u.username='sales' AND r.code='SALES' ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON u.username='accountant' AND r.code='ACCOUNTANT' ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON u.username='guide' AND r.code='GUIDE' ON CONFLICT DO NOTHING;
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON u.username='cs' AND r.code='CS' ON CONFLICT DO NOTHING;

-- ============================================================
-- Helper arrays for realism
-- Cities, Vietnamese names, vendor adjectives/types are encoded in CASE/arrays below.

-- ============================================================
-- 4) PARTNERS (100) — tên thực tế theo loại + thành phố
WITH kinds AS (
  SELECT unnest(ARRAY['Hotel','Transport','Cruise','Restaurant','Attraction','GuideAgency','Airline']) AS kind
),
cities AS (
  SELECT unnest(ARRAY['Hà Nội','Hải Phòng','Hạ Long','Ninh Bình','Đà Nẵng','Huế','Hội An','Quy Nhơn','Nha Trang','Đà Lạt','TP.HCM','Cần Thơ','Phú Quốc','Côn Đảo','Sa Pa']) AS city
)
INSERT INTO partners (id, type, name, contact, phone, email, tax_no, address, note)
SELECT
  next_id('P'),
  (SELECT kind FROM kinds OFFSET ((gs-1) % (SELECT COUNT(*) FROM kinds)) LIMIT 1),
  CASE ( (gs-1) % 7 )
    WHEN 0 THEN 'Khách sạn '       || (SELECT city FROM cities OFFSET ((gs-1) % (SELECT COUNT(*) FROM cities)) LIMIT 1)
    WHEN 1 THEN 'Vận tải '         || (SELECT city FROM cities OFFSET ((gs+2) % (SELECT COUNT(*) FROM cities)) LIMIT 1)
    WHEN 2 THEN 'Du thuyền '       || (SELECT city FROM cities OFFSET ((gs+4) % (SELECT COUNT(*) FROM cities)) LIMIT 1)
    WHEN 3 THEN 'Nhà hàng '        || (SELECT city FROM cities OFFSET ((gs+6) % (SELECT COUNT(*) FROM cities)) LIMIT 1)
    WHEN 4 THEN 'Điểm tham quan '  || (SELECT city FROM cities OFFSET ((gs+8) % (SELECT COUNT(*) FROM cities)) LIMIT 1)
    WHEN 5 THEN 'Công ty HDV '     || (SELECT city FROM cities OFFSET ((gs+10) % (SELECT COUNT(*) FROM cities)) LIMIT 1)
    ELSE      'Hãng bay '          || (SELECT city FROM cities OFFSET ((gs+12) % (SELECT COUNT(*) FROM cities)) LIMIT 1)
  END || ' #'||gs,
  'Ms./Mr. '||gs,
  '09' || lpad((7000000 + gs)::text, 8, '0'),
  'partner'||gs||'@example.com',
  '0' || lpad(gs::text, 9, '0'),
  'Số '||gs||', '||(SELECT city FROM cities OFFSET ((gs-1) % (SELECT COUNT(*) FROM cities)) LIMIT 1)||', VN',
  CASE WHEN (gs % 4)=0 THEN 'Ưu tiên' ELSE 'Chuẩn' END
FROM generate_series(1,100) AS gs
ON CONFLICT DO NOTHING;

-- ============================================================
-- 5) SERVICES (100) — tên/giá theo loại partner
WITH p AS (
  SELECT id, type, row_number() OVER (ORDER BY id) rn FROM partners
)
INSERT INTO services (id, partner_id, type, name, unit_price, capacity, note)
SELECT
  next_id('S'),
  p.id,
  CASE p.type
    WHEN 'Hotel'       THEN 'Accommodation'
    WHEN 'Transport'   THEN 'Transport'
    WHEN 'Cruise'      THEN 'Cruise'
    WHEN 'Restaurant'  THEN 'Meal'
    WHEN 'Attraction'  THEN 'Ticket'
    WHEN 'GuideAgency' THEN 'Guide'
    WHEN 'Airline'     THEN 'Flight'
    ELSE 'Service'
  END,
  CASE p.type
    WHEN 'Hotel'       THEN 'Phòng Deluxe '||rn
    WHEN 'Transport'   THEN 'Xe 29 chỗ '||rn
    WHEN 'Cruise'      THEN 'Cabin tiêu chuẩn '||rn
    WHEN 'Restaurant'  THEN 'Set menu '||rn
    WHEN 'Attraction'  THEN 'Vé tham quan '||rn
    WHEN 'GuideAgency' THEN 'HDV tiếng Việt '||rn
    WHEN 'Airline'     THEN 'Vé bay hạng Phổ thông '||rn
    ELSE 'Dịch vụ '||rn
  END,
  CASE p.type
    WHEN 'Hotel'       THEN (800000 + (rn%10)*50000)
    WHEN 'Transport'   THEN (1500000 + (rn%10)*100000)
    WHEN 'Cruise'      THEN (2200000 + (rn%10)*150000)
    WHEN 'Restaurant'  THEN (180000 + (rn%10)*20000)
    WHEN 'Attraction'  THEN (250000 + (rn%10)*20000)
    WHEN 'GuideAgency' THEN (800000 + (rn%10)*50000)
    WHEN 'Airline'     THEN (1200000 + (rn%10)*150000)
    ELSE (500000 + (rn%10)*50000)
  END::numeric,
  CASE p.type
    WHEN 'Hotel' THEN 2
    WHEN 'Cruise' THEN 2
    WHEN 'Restaurant' THEN 1
    ELSE 20
  END,
  'Auto-seeded realistic service #'||rn
FROM p
WHERE rn <= 100
ON CONFLICT DO NOTHING;

-- ============================================================
-- 6) TOURS (100) — route thực tế, days & giá “đời”
WITH routes AS (
  SELECT unnest(ARRAY[
    'Hà Nội – Hạ Long','Hà Nội – Ninh Bình','Đà Nẵng – Hội An','Huế – Đà Nẵng',
    'Nha Trang – Đà Lạt','TP.HCM – Cần Thơ','TP.HCM – Phú Quốc','Hà Nội – Sa Pa',
    'Quy Nhơn – Phú Yên','Hải Phòng – Hạ Long'
  ]) AS r
)
INSERT INTO tours (id, name, route, days, base_price, description, cover_image_url, created_at)
SELECT
  next_id('T'),
  (SELECT r FROM routes OFFSET ((gs-1) % (SELECT COUNT(*) FROM routes)) LIMIT 1) ||
    ' '||(2 + (gs%4))||'N'||(1 + (gs%4))||'Đ',
  (SELECT r FROM routes OFFSET ((gs-1) % (SELECT COUNT(*) FROM routes)) LIMIT 1),
  2 + (gs % 4),
  (2200000 + (gs % 10)*120000)::numeric,
  'Tour '||(SELECT r FROM routes OFFSET ((gs-1) % (SELECT COUNT(*) FROM routes)) LIMIT 1)||
    ' lịch trình cân đối tham quan – ăn uống – nghỉ ngơi.',
  NULL,
  CURRENT_TIMESTAMP - ((gs % 50) || ' days')::interval
FROM generate_series(1,100) AS gs
ON CONFLICT DO NOTHING;

-- ============================================================
-- 7) ITINERARIES (100) — chọn day_no hợp lệ vs tour.days
WITH t AS (
  SELECT id, days, row_number() OVER (ORDER BY id) rn FROM tours
)
INSERT INTO itineraries (id, tour_id, day_no, title, place, activity, note)
SELECT
  next_id('I'),
  t.id,
  1 + ((gs % GREATEST(t.days,1)) ),
  'Ngày '||(1 + ((gs % GREATEST(t.days,1))))||' – Hoạt động chính',
  CASE ((gs + t.rn) % 6)
    WHEN 0 THEN 'Phố cổ'
    WHEN 1 THEN 'Bãi biển'
    WHEN 2 THEN 'Bến thuyền'
    WHEN 3 THEN 'Chợ đêm'
    WHEN 4 THEN 'Làng nghề'
    ELSE 'Danh thắng'
  END,
  CASE ((gs + t.rn) % 5)
    WHEN 0 THEN 'Tham quan + chụp ảnh'
    WHEN 1 THEN 'Di chuyển + nhận phòng'
    WHEN 2 THEN 'Tự do tắm biển'
    WHEN 3 THEN 'Dùng bữa đặc sản địa phương'
    ELSE 'Tham quan kèm HDV'
  END,
  CASE WHEN (gs % 7)=0 THEN 'Ghi chú: lưu ý thời tiết.' ELSE NULL END
FROM generate_series(1,100) AS gs
JOIN t ON t.rn = ((gs-1) % (SELECT COUNT(*) FROM t)) + 1
ON CONFLICT DO NOTHING;

-- ============================================================
-- 8) CUSTOMERS (100) — tên VN, giới tính, giấy tờ
WITH last_names AS (
  SELECT unnest(ARRAY['Nguyễn','Trần','Lê','Phạm','Hoàng','Võ','Phan','Vũ','Đặng','Bùi']) AS ln
),
first_names_m AS (
  SELECT unnest(ARRAY['Anh','Dũng','Huy','Tuấn','Long','Quân','Minh','Hiếu','Nam','Phúc']) AS fn
),
first_names_f AS (
  SELECT unnest(ARRAY['Anh','Trang','Thảo','Linh','Hương','Hạnh','Nhi','Ngọc','Mai','Vy']) AS fn
)
INSERT INTO customers (id, full_name, dob, gender, id_type, id_no, phone, email, note)
SELECT
  next_id('C'),
  (SELECT ln FROM last_names OFFSET ((gs-1) % 10) LIMIT 1)||' '||
  CASE WHEN (gs % 2)=0
    THEN (SELECT fn FROM first_names_m OFFSET ((gs-1) % 10) LIMIT 1)
    ELSE (SELECT fn FROM first_names_f OFFSET ((gs-1) % 10) LIMIT 1)
  END,
  date '1970-01-01' + ((gs*97) % 18000),
  CASE WHEN (gs % 2)=0 THEN 'M' ELSE 'F' END,
  CASE WHEN (gs % 3)=0 THEN 'Passport' ELSE 'ID' END,
  CASE WHEN (gs % 3)=0 THEN 'P'||lpad(gs::text,6,'0') ELSE '0'||lpad((100000000+gs)::text,9,'0') END,
  '09' || lpad((8000000 + gs)::text, 8, '0'),
  'khach'||gs||'@mail.vn',
  CASE WHEN (gs % 12)=0 THEN 'VIP' ELSE NULL END
FROM generate_series(1,100) AS gs
ON CONFLICT DO NOTHING;

-- ============================================================
-- 9) BOOKINGS (100) — giá dựa trên base_price tour + phụ thu
WITH t AS (
  SELECT id, base_price, row_number() OVER (ORDER BY id) rn FROM tours
)
INSERT INTO bookings (id, tour_id, status, total_price, note, created_at, updated_at)
SELECT
  next_id('B'),
  t.id,
  CASE (gs % 4)
    WHEN 0 THEN 'CONFIRMED'
    WHEN 1 THEN 'REQUESTED'
    WHEN 2 THEN 'PENDING'
    ELSE 'CANCELLED'
  END,
  (t.base_price + ((gs % 5) * 150000))::numeric,
  'Booking code BK'||lpad(gs::text,3,'0'),
  CURRENT_TIMESTAMP - ((gs % 35) || ' days')::interval,
  CURRENT_TIMESTAMP - ((gs % 20) || ' days')::interval
FROM generate_series(1,100) AS gs
JOIN t ON t.rn = ((gs-1) % (SELECT COUNT(*) FROM t)) + 1
ON CONFLICT DO NOTHING;

-- ============================================================
-- 10) BOOKING_CUSTOMERS (100) — 1 khách lead/booking
WITH b AS (SELECT id, row_number() OVER (ORDER BY id) rn FROM bookings),
     c AS (SELECT id, row_number() OVER (ORDER BY id) rn FROM customers)
INSERT INTO booking_customers (booking_id, customer_id, role)
SELECT
  (SELECT id FROM b WHERE rn = gs),
  (SELECT id FROM c WHERE rn = ((gs-1) % (SELECT COUNT(*) FROM c)) + 1),
  'LEAD'
FROM generate_series(1,100) AS gs
ON CONFLICT (booking_id, customer_id) DO NOTHING;

-- ============================================================
-- 11) PAYMENTS (100) — chỉ xoay vòng trên booking không CANCELLED
WITH b AS (
  SELECT id, row_number() OVER (ORDER BY id) rn
  FROM bookings WHERE status <> 'CANCELLED'
)
INSERT INTO payments (id, booking_id, type, amount, paid_at, note)
SELECT
  next_id('PY'),
  (SELECT id FROM b WHERE rn = ((gs-1) % GREATEST((SELECT COUNT(*) FROM b),1)) + 1),
  CASE (gs % 3)
    WHEN 0 THEN 'CASH'
    WHEN 1 THEN 'TRANSFER'
    ELSE 'CARD'
  END,
  (1000000 + (gs % 20)*200000)::numeric,
  CURRENT_TIMESTAMP - ((gs % 40) || ' days')::interval,
  'Thanh toán bằng '||
  CASE (gs % 3)
    WHEN 0 THEN 'tiền mặt'
    WHEN 1 THEN 'chuyển khoản'
    ELSE 'thẻ'
  END||' #'||gs
FROM generate_series(1,100) AS gs
ON CONFLICT DO NOTHING;

-- ============================================================
-- 12) INVOICES (100) — chỉ xoay vòng trên booking CONFIRMED
WITH b AS (
  SELECT id, row_number() OVER (ORDER BY id) rn
  FROM bookings WHERE status='CONFIRMED'
)
INSERT INTO invoices (id, booking_id, no, amount, vat, issued_at, pdf_path)
SELECT
  next_id('IN'),
  (SELECT id FROM b WHERE rn = ((gs-1) % GREATEST((SELECT COUNT(*) FROM b),1)) + 1),
  'INV-2025-'||lpad(gs::text,3,'0'),
  (3500000 + (gs % 25)*250000)::numeric,
  ((3500000 + (gs % 25)*250000) * 0.10)::numeric,
  CURRENT_DATE - (gs % 60),
  '/invoices/INV-2025-'||lpad(gs::text,3,'0')||'.pdf'
FROM generate_series(1,100) AS gs
ON CONFLICT (no) DO NOTHING;

-- ============================================================
-- 13) ALLOCATIONS (100) — gán dịch vụ theo ngày hợp lý
WITH b AS (SELECT id, row_number() OVER (ORDER BY id) rn FROM bookings WHERE status <> 'CANCELLED'),
     s AS (SELECT id, row_number() OVER (ORDER BY id) rn FROM services)
INSERT INTO allocations (id, booking_id, day_no, service_id, detail)
SELECT
  next_id('A'),
  (SELECT id FROM b WHERE rn = ((gs-1) % GREATEST((SELECT COUNT(*) FROM b),1)) + 1),
  1 + (gs % 3),
  (SELECT id FROM s WHERE rn = ((gs-1) % GREATEST((SELECT COUNT(*) FROM s),1)) + 1),
  jsonb_build_object('qty', 1 + (gs % 4), 'note', 'Dịch vụ ngày '||(1 + (gs % 3)))
FROM generate_series(1,100) AS gs
ON CONFLICT DO NOTHING;

-- ============================================================
-- 14) PAYABLES (100) — công nợ vendor
WITH p AS (SELECT id, row_number() OVER (ORDER BY id) rn FROM partners),
     b AS (SELECT id, row_number() OVER (ORDER BY id) rn FROM bookings WHERE status <> 'CANCELLED')
INSERT INTO payables (id, partner_id, booking_id, amount, due_date, status)
SELECT
  next_id('PL'),
  (SELECT id FROM p WHERE rn = ((gs-1) % GREATEST((SELECT COUNT(*) FROM p),1)) + 1),
  (SELECT id FROM b WHERE rn = ((gs-1) % GREATEST((SELECT COUNT(*) FROM b),1)) + 1),
  (700000 + (gs % 30)*50000)::numeric,
  CURRENT_DATE + ((gs % 45))::int,
  CASE (gs % 3) WHEN 0 THEN 'PENDING' WHEN 1 THEN 'APPROVED' ELSE 'PAID' END
FROM generate_series(1,100) AS gs
ON CONFLICT DO NOTHING;

-- ============================================================
-- 15) EXPENSES (100) — chi phí HDV
WITH b AS (SELECT id, row_number() OVER (ORDER BY id) rn FROM bookings WHERE status <> 'CANCELLED'),
     u AS (SELECT id FROM users WHERE username='guide' LIMIT 1)
INSERT INTO expenses (id, booking_id, guide_id, amount, category, note, spent_at)
SELECT
  next_id('EX'),
  (SELECT id FROM b WHERE rn = ((gs-1) % GREATEST((SELECT COUNT(*) FROM b),1)) + 1),
  (SELECT id FROM u),
  (120000 + (gs % 20)*15000)::numeric,
  CASE (gs % 4) WHEN 0 THEN 'Meal' WHEN 1 THEN 'Taxi' WHEN 2 THEN 'Ticket' ELSE 'Misc' END,
  'Chi phí phát sinh #'||gs,
  CURRENT_DATE - (gs % 25)
FROM generate_series(1,100) AS gs
ON CONFLICT DO NOTHING;

-- ============================================================
-- 16) AUDIT_LOGS (100) — đa dạng action/entity
WITH u AS (SELECT id, row_number() OVER (ORDER BY id) rn FROM users),
     e AS (
       SELECT unnest(ARRAY['User','Tour','Booking','Payment','Invoice','Customer','Partner','Service']) AS ent
     )
INSERT INTO audit_logs (id, user_id, action, entity, entity_id, at, meta)
SELECT
  next_id('AL'),
  (SELECT id FROM u WHERE rn = ((gs-1) % (SELECT COUNT(*) FROM u)) + 1),
  CASE (gs % 5) WHEN 0 THEN 'CREATE' WHEN 1 THEN 'UPDATE' WHEN 2 THEN 'DELETE' WHEN 3 THEN 'READ' ELSE 'EXPORT' END,
  (SELECT ent FROM e OFFSET (gs % (SELECT COUNT(*) FROM e)) LIMIT 1),
  NULL,
  CURRENT_TIMESTAMP - (gs || ' minutes')::interval,
  jsonb_build_object('seed','realistic','index',gs)
FROM generate_series(1,100) AS gs
ON CONFLICT DO NOTHING;

-- ============================================================
-- END V2 REWRITE (realistic, 100 rows each table)
