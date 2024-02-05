INSERT INTO users
(id, username, password, email, role)
VALUES
    (1, 'admin', 'pass', 'admin@mail.ru', 'ROLE_ADMIN'),
    (2, 'user', 'pass', 'user@mail.ru', 'ROLE_USER');

SELECT SETVAL('users_id_seq', (SELECT MAX(id) from users));