CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50)
);

INSERT INTO usuarios (id, email, senha, perfil) VALUES 
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a99', 'admin@email.com', '$2b$12$V6XaIh8A.L9uUAezCabSJOhpnScNzHPaT99kwkTzegR3lJdJnaW7O', 'ADMIN');
