CREATE TABLE artistas (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE albuns (
    id UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    ano_lancamento INTEGER,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE artista_album (
    artista_id UUID NOT NULL,
    album_id UUID NOT NULL,
    PRIMARY KEY (artista_id, album_id),
    FOREIGN KEY (artista_id) REFERENCES artistas(id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES albuns(id) ON DELETE CASCADE
);

CREATE TABLE album_imagens (
    album_id UUID NOT NULL,
    url_imagem VARCHAR(500) NOT NULL,
    FOREIGN KEY (album_id) REFERENCES albuns(id) ON DELETE CASCADE
);

CREATE INDEX idx_artista_nome ON artistas(nome);
CREATE INDEX idx_album_titulo ON albuns(titulo);
