-- =====================================================================
-- Student Course Registration System - PostgreSQL schema
--
-- One table per old .txt file. The app runs this file automatically on
-- every start-up (spring.sql.init.mode=always). "IF NOT EXISTS" makes it
-- safe to run again and again: it never deletes or changes your data.
--
-- IDs keep the same format the app already uses (S001, CRS1779..., ...).
-- created_at is only used to show rows in the order they were added,
-- the same order the .txt files had.
-- =====================================================================

-- ---------- People ----------

CREATE TABLE IF NOT EXISTS students (
    student_id      VARCHAR(50)  PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    address         VARCHAR(255) NOT NULL,
    password        VARCHAR(100) NOT NULL,          -- BCrypt hash, never plain text
    semester        INTEGER      NOT NULL,
    enrollment_date DATE         NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS lecturers (
    lecturer_id     VARCHAR(50)  PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    address         VARCHAR(255) NOT NULL,
    password        VARCHAR(100) NOT NULL,          -- BCrypt hash
    department      VARCHAR(100) NOT NULL,
    specialization  VARCHAR(100) NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS admins (
    admin_id        VARCHAR(50)  PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    address         VARCHAR(255) NOT NULL,
    password        VARCHAR(100) NOT NULL,          -- BCrypt hash
    access_level    INTEGER      NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT clock_timestamp()
);

-- ---------- Courses and rooms ----------

CREATE TABLE IF NOT EXISTS course_categories (
    category_id     VARCHAR(50)  PRIMARY KEY,
    category_name   VARCHAR(100) NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS courses (
    course_id         VARCHAR(50)  PRIMARY KEY,
    course_name       VARCHAR(150) NOT NULL,
    credits           INTEGER      NOT NULL,
    description       TEXT         NOT NULL DEFAULT '',
    max_students      INTEGER      NOT NULL,
    enrolled_students INTEGER      NOT NULL DEFAULT 0,
    -- RESTRICT: a category that still has courses cannot be deleted
    category_id       VARCHAR(50)  NOT NULL REFERENCES course_categories(category_id) ON DELETE RESTRICT,
    created_at        TIMESTAMP    NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id         VARCHAR(50)  PRIMARY KEY,
    room_name       VARCHAR(100) NOT NULL,
    capacity        INTEGER      NOT NULL,
    available       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT clock_timestamp()
);

-- ---------- Things that link people and courses ----------
-- ON DELETE CASCADE: when a student/course/lecturer/room is deleted, the rows
-- that point to it are deleted too. The old .txt version simply hid those
-- "orphan" rows, so the screens look exactly the same.

CREATE TABLE IF NOT EXISTS registrations (
    registration_id   VARCHAR(50) PRIMARY KEY,
    student_id        VARCHAR(50) NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    course_id         VARCHAR(50) NOT NULL REFERENCES courses(course_id)   ON DELETE CASCADE,
    registration_date DATE        NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                      CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at        TIMESTAMP   NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS schedules (
    schedule_id     VARCHAR(50) PRIMARY KEY,
    course_id       VARCHAR(50) NOT NULL REFERENCES courses(course_id)     ON DELETE CASCADE,
    lecturer_id     VARCHAR(50) NOT NULL REFERENCES lecturers(lecturer_id) ON DELETE CASCADE,
    day             VARCHAR(20) NOT NULL,
    start_time      TIME        NOT NULL,
    end_time        TIME        NOT NULL,
    room_id         VARCHAR(50) NOT NULL REFERENCES rooms(room_id)         ON DELETE CASCADE,
    created_at      TIMESTAMP   NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id        VARCHAR(50)   PRIMARY KEY,
    student_id        VARCHAR(50)   NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    amount            NUMERIC(12,2) NOT NULL,
    payment_date      DATE          NOT NULL,
    method            VARCHAR(20)   NOT NULL
                      CHECK (method IN ('CASH', 'CARD', 'BANK_TRANSFER', 'ONLINE')),
    status            VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                      CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    receipt_file_name VARCHAR(255),                   -- points to uploaded_files.file_name
    created_at        TIMESTAMP     NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS feedbacks (
    feedback_id     VARCHAR(50) PRIMARY KEY,
    student_id      VARCHAR(50) NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
    course_id       VARCHAR(50) NOT NULL REFERENCES courses(course_id)   ON DELETE CASCADE,
    rating          INTEGER     NOT NULL,
    comment         TEXT        NOT NULL DEFAULT '',
    feedback_date   DATE        NOT NULL,
    submitted       BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP   NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE IF NOT EXISTS course_materials (
    material_id     VARCHAR(50)  PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    file_type       VARCHAR(20)  NOT NULL DEFAULT '',
    course_id       VARCHAR(50)  NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    file_name       VARCHAR(255) NOT NULL DEFAULT '',   -- points to uploaded_files.file_name
    created_at      TIMESTAMP    NOT NULL DEFAULT clock_timestamp()
);

-- ---------- Uploaded files (payment receipts, course materials) ----------
-- Free hosts wipe their disk on every restart, so the file bytes live here
-- instead of in the uploads/ folder.

CREATE TABLE IF NOT EXISTS uploaded_files (
    folder          VARCHAR(20)  NOT NULL CHECK (folder IN ('payments', 'materials')),
    file_name       VARCHAR(255) NOT NULL,
    content_type    VARCHAR(150),
    data            BYTEA        NOT NULL,
    uploaded_at     TIMESTAMP    NOT NULL DEFAULT clock_timestamp(),
    PRIMARY KEY (folder, file_name)
);

-- Indexes for the "find by student / course / lecturer" look-ups
CREATE INDEX IF NOT EXISTS idx_registrations_student ON registrations(student_id);
CREATE INDEX IF NOT EXISTS idx_registrations_course  ON registrations(course_id);
CREATE INDEX IF NOT EXISTS idx_schedules_course      ON schedules(course_id);
CREATE INDEX IF NOT EXISTS idx_schedules_lecturer    ON schedules(lecturer_id);
CREATE INDEX IF NOT EXISTS idx_payments_student      ON payments(student_id);
CREATE INDEX IF NOT EXISTS idx_feedbacks_student     ON feedbacks(student_id);
CREATE INDEX IF NOT EXISTS idx_feedbacks_course      ON feedbacks(course_id);
CREATE INDEX IF NOT EXISTS idx_materials_course      ON course_materials(course_id);
CREATE INDEX IF NOT EXISTS idx_courses_category      ON courses(category_id);
