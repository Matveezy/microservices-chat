INSERT INTO messages
    (id, body, sender_id, chat_id)
VALUES (1, 'Hello everybody', 1, 1),
       (2, 'Hello there', 2, 1),
       (3, 'Hello Bob!', 3, 2),
       (4, 'Hello Mike!', 4, 2);

SELECT SETVAL('messages_id_seq', (SELECT MAX(id) from messages));

INSERT INTO message_deliveries
    (message_id, receiver_id, delivered)
VALUES (1, 2, false),
       (1, 3, false),
       (2, 1, false),
       (2, 3, false),
       (3, 4, false),
       (4, 3, false);