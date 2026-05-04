INSERT INTO usuario (id, nome, email, senha, role)
VALUES (1, 'Usuario Demo', 'demo@zarka.com', '123456', 'ROLE_USER')
ON CONFLICT (id) DO NOTHING;

INSERT INTO produto (id, nome, preco)
VALUES (1, 'Whey Protein', 149.90)
ON CONFLICT (id) DO NOTHING;

INSERT INTO produto (id, nome, preco)
VALUES (2, 'Creatina', 89.90)
ON CONFLICT (id) DO NOTHING;

INSERT INTO produto (id, nome, preco)
VALUES (3, 'Pre Treino', 69.90)
ON CONFLICT (id) DO NOTHING;
