-- Repair ID defaults for migrated schemas where auto-increment was lost
CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;
ALTER TABLE users ALTER COLUMN id SET DEFAULT nextval('users_id_seq');
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 0) + 1, false);

CREATE SEQUENCE IF NOT EXISTS user_role_user_role_id_seq START WITH 1 INCREMENT BY 1;
ALTER TABLE user_role ALTER COLUMN user_role_id SET DEFAULT nextval('user_role_user_role_id_seq');
SELECT setval('user_role_user_role_id_seq', COALESCE((SELECT MAX(user_role_id) FROM user_role), 0) + 1, false);

CREATE SEQUENCE IF NOT EXISTS category_cid_seq START WITH 1 INCREMENT BY 1;
ALTER TABLE category ALTER COLUMN cid SET DEFAULT nextval('category_cid_seq');
SELECT setval('category_cid_seq', COALESCE((SELECT MAX(cid) FROM category), 0) + 1, false);

CREATE SEQUENCE IF NOT EXISTS quiz_q_id_seq START WITH 1 INCREMENT BY 1;
ALTER TABLE quiz ALTER COLUMN q_id SET DEFAULT nextval('quiz_q_id_seq');
SELECT setval('quiz_q_id_seq', COALESCE((SELECT MAX(q_id) FROM quiz), 0) + 1, false);

CREATE SEQUENCE IF NOT EXISTS question_ques_id_seq START WITH 1 INCREMENT BY 1;
ALTER TABLE question ALTER COLUMN ques_id SET DEFAULT nextval('question_ques_id_seq');
SELECT setval('question_ques_id_seq', COALESCE((SELECT MAX(ques_id) FROM question), 0) + 1, false);

-- Audit columns for core entities
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
UPDATE users SET created_at = NOW() WHERE created_at IS NULL;
UPDATE users SET updated_at = NOW() WHERE updated_at IS NULL;
UPDATE users SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE users SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;

ALTER TABLE category ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;
ALTER TABLE category ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;
ALTER TABLE category ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE category ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
UPDATE category SET created_at = NOW() WHERE created_at IS NULL;
UPDATE category SET updated_at = NOW() WHERE updated_at IS NULL;
UPDATE category SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE category SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;

ALTER TABLE quiz ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
UPDATE quiz SET created_at = NOW() WHERE created_at IS NULL;
UPDATE quiz SET updated_at = NOW() WHERE updated_at IS NULL;
UPDATE quiz SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE quiz SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;

ALTER TABLE question ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;
ALTER TABLE question ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;
ALTER TABLE question ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
ALTER TABLE question ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
UPDATE question SET created_at = NOW() WHERE created_at IS NULL;
UPDATE question SET updated_at = NOW() WHERE updated_at IS NULL;
UPDATE question SET created_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE question SET updated_by = 'SYSTEM' WHERE updated_by IS NULL;

-- Seed ADMIN role if missing
INSERT INTO roles (role_id, role_name)
SELECT 44, 'ADMIN'
WHERE NOT EXISTS (
  SELECT 1
  FROM roles
  WHERE role_id = 44 OR role_name = 'ADMIN'
);

-- Seed admin user if missing
-- username: admin
-- password: admin123 (BCrypt)
INSERT INTO users (id, username, password, first_name, last_name, email, phone, enabled, profile)
SELECT
  COALESCE((SELECT MAX(id) + 1 FROM users), 1),
  'admin',
  '$2y$10$e0Dm.PrelTgEa7L3yUPOZuwdeM5XwXv/7HJ3dJnaRJlm7kPQVKQa6',
  'System',
  'Admin',
  'admin@example.com',
  '',
  true,
  'default.png'
WHERE NOT EXISTS (
  SELECT 1
  FROM users
  WHERE username = 'admin'
);

-- If users.id has a sequence default, align it after manual insert
SELECT setval(
  pg_get_serial_sequence('users', 'id'),
  COALESCE((SELECT MAX(id) FROM users), 1),
  true
)
WHERE pg_get_serial_sequence('users', 'id') IS NOT NULL;

-- Link admin user to ADMIN role if missing
INSERT INTO user_role (user_role_id, user_id, role_role_id)
SELECT
  COALESCE((SELECT MAX(user_role_id) + 1 FROM user_role), 1),
  u.id,
  r.role_id
FROM users u
JOIN roles r ON r.role_name = 'ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1
    FROM user_role ur
    WHERE ur.user_id = u.id
      AND ur.role_role_id = r.role_id
  );

-- If user_role.user_role_id has a sequence default, align it after manual insert
SELECT setval(
  pg_get_serial_sequence('user_role', 'user_role_id'),
  COALESCE((SELECT MAX(user_role_id) FROM user_role), 1),
  true
)
WHERE pg_get_serial_sequence('user_role', 'user_role_id') IS NOT NULL;
