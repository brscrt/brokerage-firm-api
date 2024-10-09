INSERT INTO public.customer (password, role, username)
VALUES ('$2a$10$/JKMHDU2uFsQNMCv0LDl9OGTKF/Rf2E2FEKrAykgTSIeneOsPEMuO', 'USER', 'user1'),
       ('$2a$10$pmvTnQMiF3p0sGgEMwsY/uAOdSi/IXvIS6M4aujPkWeKp1fZgGTi2', 'USER', 'user2'),
       ('$2a$10$SjOhvJMmxemNNhNcpzMcruFSuwXSmozg0JXm0AatnaZGD4rpBCCeK', 'ADMIN', 'admin1'),
       ('$2a$10$Hnsf4U65AkcxQfsgGJzq2eKr/W5L0s.Wcp.XJ075kjkXhQC26P5.m', 'USER', 'user3');

INSERT INTO asset (asset_name, size, usable_size, customer_id)
VALUES ('Gold', 100.5, 50.5, 1),
       ('TRY', 200.0, 150.0, 1),
       ('TRY', 75.0, 30.0, 2),
       ('TRY', 50.0, 50.0, 3),
       ('Platinum', 75.0, 30.0, 3),
       ('TRY', 0, 0, 4);