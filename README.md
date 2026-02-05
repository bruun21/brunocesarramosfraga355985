# Desafio Fullstack - Gerenciador de Álbuns de Artistas

Este projeto é uma API RESTful desenvolvida em Java com Spring Boot para gerenciamento de artistas e seus álbuns musicais. A aplicação inclui funcionalidades avançadas como upload de imagens (MinIO), atualizações em tempo real (WebSocket) e uma suíte robusta de testes.

## Tecnologias Utilizadas

- **Java 21** & **Spring Boot 3.2.1**
- **PostgreSQL**: Banco de dados relacional.
- **MinIO**: Object Storage compatível com S3 para armazenamento de capas de álbuns.
- **WebSocket (STOMP)**: Notificações em tempo real para o cliente.
- **Flyway**: Migrações de banco de dados.
- **Docker & Docker Compose**: Orquestração de containers.
- **JUnit 5 & Mockito**: Testes unitários e de integração.
- **Swagger/OpenAPI**: Documentação viva da API.

## Configuração e Execução

### Pré-requisitos
- Docker e Docker Compose instalados.
- JDK 21 e Maven (opcional, se quiser rodar fora do Docker).

### Passo a Passo

1. **Subir Infraestrutura (Banco de Dados e MinIO)**
   ```bash
   docker-compose up -d
   ```
   Isso iniciará o PostgreSQL (porta 5432) e o MinIO (Console: 9001, API: 9000).

2. **Executar a Aplicação (Backend)**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   A aplicação estará disponível em `http://localhost:8080`.

3. **Acessar Documentação da API**
   - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - OpenAPI Json: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Funcionalidades Principais

### API REST
- **Artistas**: CRUD completo (`/api/artistas`). Suporta paginação e ordenação.
- **Álbuns**: CRUD completo (`/api/albuns`). Associado a artistas.

### Upload & Gráficos (MinIO)
- Endpoint: `POST /api/albuns/{id}/imagem`
- Permite upload de capas de álbuns. A imagem é salva no Bucket `album-covers` do MinIO e a URL pública é armazenada no banco.

### Real-time (WebSocket)
- Endpoint de conexão: `ws://localhost:8080/ws`
- Tópico de assinatura: `/topic/albuns/{id}`
- Sempre que uma imagem é adicionada a um álbum, uma mensagem JSON com os dados atualizados do álbum é enviada para este tópico.

## Testes

O projeto possui cobertura de testes unitários (Services) e de integração (Controllers).

```bash
cd backend
mvn test
```

## Decisões de Arquitetura

### Uso do Nginx (Reverse Proxy)
O Nginx foi adotado como porta de entrada única (Gateway) da infraestrutura.
- **Roteamento Unificado**: Redireciona requisições `/api` para o backend e gerencia o acesso a recursos estáticos.
- **Simplicidade de Setup**: Evita conflitos de CORS em ambiente de desenvolvimento ao servir tudo na mesma origem (localhost:80).
- **Produção**: Facilita a implementação futura de HTTPS e Load Balancing.

### Armazenamento com MinIO
Para o upload de arquivos, escolheu-se o MinIO por ser compatível com a API do **Amazon S3**.
- **Portabilidade**: O código desenvolvido (`MinioService`) funciona transparentemente tanto localmente quanto na AWS/GCP/Azure, bastando alterar as credenciais.
- **Isolamento**: Evita salvar arquivos no sistema de arquivos do container, o que seria efêmero e difícil de escalar.

## Estrutura do Projeto

```
backend/
├── src/main/java/com/bruno/artistalbum/
│   ├── config/       # Configs (MinIO, WebSocket, Security)
│   ├── controller/   # Camada REST
│   ├── dto/          # Data Transfer Objects
│   ├── model/        # Entidades JPA
│   ├── repository/   # Interfaces Repository
│   └── service/      # Regras de Negócio e Integrações
├── src/main/resources/
│   ├── db/migration/ # Scripts SQL do Flyway
│   └── application.properties
└── src/test/         # Testes Automatizados
```

---
Desenvolvido por Bruno César Ramos Fraga.
