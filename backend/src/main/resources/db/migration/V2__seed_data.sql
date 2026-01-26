-- Insert Artistas
INSERT INTO artistas (id, nome) VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Pink Floyd'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'The Beatles'),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Led Zeppelin');

-- Insert Albuns
INSERT INTO albuns (id, titulo, ano_lancamento) VALUES 
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'The Dark Side of the Moon', 1973),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Abbey Road', 1969),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'Led Zeppelin IV', 1971);

-- Link Artistas and Albuns
INSERT INTO artista_album (artista_id, album_id) VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21'), -- Pink Floyd -> Dark Side
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), -- Beatles -> Abbey Road
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23'); -- Led Zeppelin -> IV
