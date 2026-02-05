CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50)
);

INSERT INTO usuarios (id, email, senha, perfil) VALUES 
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a99', 'admin@email.com', '$2a$10$KnUWjaUPxe9cos5hXdTkZOAHotCYsCO4qMsKSMWrDxokjG.aTeHji', 'ADMIN');
-- Senha: admin123
