-- Adiciona coluna tipo na tabela artistas
ALTER TABLE artistas ADD COLUMN tipo VARCHAR(20) DEFAULT 'BANDA';
ALTER TABLE artistas ALTER COLUMN tipo SET NOT NULL;

-- Cria tabela de regionais
CREATE TABLE regionais (
    internal_id SERIAL PRIMARY KEY,
    id INTEGER NOT NULL, -- ID que vem do endpoint
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Insere dados de exemplo do edital
INSERT INTO artistas (id, nome, tipo) VALUES 
(gen_random_uuid(), 'Serj Tankian', 'CANTOR'),
(gen_random_uuid(), 'Mike Shinoda', 'CANTOR'),
(gen_random_uuid(), 'Michel Teló', 'CANTOR');

-- O Guns N Roses já existe, vamos atualizar o tipo dele para BANDA (já é o default)
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Guns N Roses';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Queen';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'AC/DC';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Nirvana';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Metallica';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Iron Maiden';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Black Sabbath';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'The Rolling Stones';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'The Who';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'U2';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Coldplay';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Radiohead';
UPDATE artistas SET tipo = 'BANDA' WHERE nome = 'Daft Punk';
