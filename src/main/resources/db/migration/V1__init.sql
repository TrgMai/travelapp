-- V1__init.sql: initialize schema, sequences and triggers
-- Note: this migration intentionally does not create any indexes beyond primary keys.

-- Drop existing schema if exists (safe for idempotency)
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS partners CASCADE;
DROP TABLE IF EXISTS services CASCADE;
DROP TABLE IF EXISTS tours CASCADE;
DROP TABLE IF EXISTS itineraries CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS booking_customers CASCADE;
DROP TABLE IF EXISTS allocations CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS invoices CASCADE;
DROP TABLE IF EXISTS payables CASCADE;
DROP TABLE IF EXISTS expenses CASCADE;
DROP TABLE IF EXISTS id_sequences CASCADE;

-- Table for prefix sequences
CREATE TABLE id_sequences (
    prefix VARCHAR PRIMARY KEY,
    next_num BIGINT NOT NULL
);

-- Function to generate next id with prefix
CREATE OR REPLACE FUNCTION next_id(p_prefix VARCHAR, p_width INT DEFAULT 7)
RETURNS VARCHAR AS
$$
DECLARE
    seq BIGINT;
    result VARCHAR;
BEGIN
    -- Acquire the row for the prefix, insert if not exists
    LOOP
        UPDATE id_sequences SET next_num = next_num + 1 WHERE prefix = p_prefix RETURNING next_num INTO seq;
        IF FOUND THEN
            EXIT;
        END IF;
        BEGIN
            INSERT INTO id_sequences(prefix, next_num) VALUES (p_prefix, 1);
            seq := 1;
            EXIT;
        EXCEPTION WHEN unique_violation THEN
            -- concurrent insert, retry
        END;
    END LOOP;
    result := p_prefix || LPAD(seq::TEXT, p_width, '0');
    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Security tables
CREATE TABLE users (
    id VARCHAR PRIMARY KEY,
    username VARCHAR UNIQUE NOT NULL,
    password_hash VARCHAR NOT NULL,
    full_name VARCHAR NOT NULL,
    email VARCHAR,
    phone VARCHAR,
    status VARCHAR NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id VARCHAR PRIMARY KEY,
    code VARCHAR UNIQUE NOT NULL,
    name VARCHAR NOT NULL
);

CREATE TABLE permissions (
    id VARCHAR PRIMARY KEY,
    code VARCHAR UNIQUE NOT NULL,
    name VARCHAR NOT NULL
);

CREATE TABLE user_roles (
    user_id VARCHAR NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id VARCHAR NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id VARCHAR NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id VARCHAR NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE audit_logs (
    id VARCHAR PRIMARY KEY,
    user_id VARCHAR REFERENCES users(id),
    action VARCHAR NOT NULL,
    entity VARCHAR NOT NULL,
    entity_id VARCHAR,
    at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    meta JSONB
);

-- Domain tables
CREATE TABLE customers (
    id VARCHAR PRIMARY KEY,
    full_name VARCHAR NOT NULL,
    dob DATE,
    gender VARCHAR,
    id_type VARCHAR,
    id_no VARCHAR,
    phone VARCHAR,
    email VARCHAR,
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE partners (
    id VARCHAR PRIMARY KEY,
    type VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    contact VARCHAR,
    phone VARCHAR,
    email VARCHAR,
    tax_no VARCHAR,
    address VARCHAR,
    note TEXT
);

CREATE TABLE services (
    id VARCHAR PRIMARY KEY,
    partner_id VARCHAR REFERENCES partners(id),
    type VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    unit_price NUMERIC,
    capacity INT,
    note TEXT
);

CREATE TABLE tours (
    id VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL,
    route VARCHAR,
    days INT,
    base_price NUMERIC,
    description TEXT,
    cover_image_url VARCHAR,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE itineraries (
    id VARCHAR PRIMARY KEY,
    tour_id VARCHAR NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    day_no INT NOT NULL,
    title VARCHAR,
    place VARCHAR,
    activity TEXT,
    note TEXT
);

CREATE TABLE bookings (
    id VARCHAR PRIMARY KEY,
    tour_id VARCHAR NOT NULL REFERENCES tours(id),
    status VARCHAR NOT NULL,
    total_price NUMERIC,
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE booking_customers (
    booking_id VARCHAR NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    customer_id VARCHAR NOT NULL REFERENCES customers(id),
    role VARCHAR,
    PRIMARY KEY (booking_id, customer_id)
);

CREATE TABLE allocations (
    id VARCHAR PRIMARY KEY,
    booking_id VARCHAR NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    day_no INT NOT NULL,
    service_id VARCHAR NOT NULL REFERENCES services(id),
    detail JSONB
);

-- Finance tables
CREATE TABLE payments (
    id VARCHAR PRIMARY KEY,
    booking_id VARCHAR NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    type VARCHAR NOT NULL,
    amount NUMERIC NOT NULL,
    paid_at TIMESTAMP NOT NULL,
    note TEXT
);

CREATE TABLE invoices (
    id VARCHAR PRIMARY KEY,
    booking_id VARCHAR NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    no VARCHAR UNIQUE NOT NULL,
    amount NUMERIC NOT NULL,
    vat NUMERIC,
    issued_at TIMESTAMP NOT NULL,
    pdf_path VARCHAR
);

CREATE TABLE payables (
    id VARCHAR PRIMARY KEY,
    partner_id VARCHAR NOT NULL REFERENCES partners(id),
    booking_id VARCHAR REFERENCES bookings(id),
    amount NUMERIC NOT NULL,
    due_date DATE,
    status VARCHAR
);

CREATE TABLE expenses (
    id VARCHAR PRIMARY KEY,
    booking_id VARCHAR NOT NULL REFERENCES bookings(id),
    guide_id VARCHAR REFERENCES users(id),
    amount NUMERIC NOT NULL,
    category VARCHAR,
    note TEXT,
    spent_at DATE
);

-- Triggers to auto-populate IDs with prefix per table

CREATE OR REPLACE FUNCTION assign_id_customers()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('C');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER customers_id_trigger BEFORE INSERT ON customers
FOR EACH ROW EXECUTE FUNCTION assign_id_customers();

CREATE OR REPLACE FUNCTION assign_id_partners()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('P');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER partners_id_trigger BEFORE INSERT ON partners
FOR EACH ROW EXECUTE FUNCTION assign_id_partners();

CREATE OR REPLACE FUNCTION assign_id_services()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('S');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER services_id_trigger BEFORE INSERT ON services
FOR EACH ROW EXECUTE FUNCTION assign_id_services();

CREATE OR REPLACE FUNCTION assign_id_tours()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('T');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER tours_id_trigger BEFORE INSERT ON tours
FOR EACH ROW EXECUTE FUNCTION assign_id_tours();

CREATE OR REPLACE FUNCTION assign_id_itineraries()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('I');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER itineraries_id_trigger BEFORE INSERT ON itineraries
FOR EACH ROW EXECUTE FUNCTION assign_id_itineraries();

CREATE OR REPLACE FUNCTION assign_id_bookings()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('B');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER bookings_id_trigger BEFORE INSERT ON bookings
FOR EACH ROW EXECUTE FUNCTION assign_id_bookings();

CREATE OR REPLACE FUNCTION assign_id_allocations()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('A');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER allocations_id_trigger BEFORE INSERT ON allocations
FOR EACH ROW EXECUTE FUNCTION assign_id_allocations();

CREATE OR REPLACE FUNCTION assign_id_payments()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('PY');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER payments_id_trigger BEFORE INSERT ON payments
FOR EACH ROW EXECUTE FUNCTION assign_id_payments();

CREATE OR REPLACE FUNCTION assign_id_invoices()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('IN');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER invoices_id_trigger BEFORE INSERT ON invoices
FOR EACH ROW EXECUTE FUNCTION assign_id_invoices();

CREATE OR REPLACE FUNCTION assign_id_payables()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('PL');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER payables_id_trigger BEFORE INSERT ON payables
FOR EACH ROW EXECUTE FUNCTION assign_id_payables();

CREATE OR REPLACE FUNCTION assign_id_expenses()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('EX');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER expenses_id_trigger BEFORE INSERT ON expenses
FOR EACH ROW EXECUTE FUNCTION assign_id_expenses();

CREATE OR REPLACE FUNCTION assign_id_users()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('U');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER users_id_trigger BEFORE INSERT ON users
FOR EACH ROW EXECUTE FUNCTION assign_id_users();

CREATE OR REPLACE FUNCTION assign_id_roles()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('R');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER roles_id_trigger BEFORE INSERT ON roles
FOR EACH ROW EXECUTE FUNCTION assign_id_roles();

CREATE OR REPLACE FUNCTION assign_id_permissions()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('PR');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER permissions_id_trigger BEFORE INSERT ON permissions
FOR EACH ROW EXECUTE FUNCTION assign_id_permissions();

CREATE OR REPLACE FUNCTION assign_id_audit_logs()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.id IS NULL THEN
        NEW.id := next_id('AL');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER audit_logs_id_trigger BEFORE INSERT ON audit_logs
FOR EACH ROW EXECUTE FUNCTION assign_id_audit_logs();