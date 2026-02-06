# Projeto: Artist Album Manager (Desafio Sênior - SEPLAG/IOMAT)

Este projeto é uma solução completa para o gerenciamento de artistas e álbuns, desenvolvida para atender aos requisitos técnicos de nível **Sênior** do edital IOMAT/SEPLAG. A aplicação foca em robustez, segurança, escalabilidade e integração com serviços externos.

---

## 📋 Informações do Candidato
- **Nome:** Bruno César Ramos Fraga
- **Cargo:** Desenvolvedor Backend Java (Sênior)

---

## 🚀 Tecnologias e Arquitetura

### Stack Tecnológica
- **Backend:** Java 17+ (Spring Boot 3.2.1)
- **Banco de Dados:** PostgreSQL (Relacional)
- **Object Storage:** MinIO (Compatível com S3)
- **Mensageria/Real-time:** WebSocket (STOMP/SockJS)
- **Migrações:** Flyway
- **Documentação:** OpenAPI 3 / Swagger
- **Monitoramento:** Spring Actuator (Health, Liveness, Readiness)
- **Segurança:** JWT com Refresh Token e Rate Limiting

## 🧠 Decisões Técnicas e Racional

### 1. Segurança e Autenticação (Requisitos Sênior A/B)
- **JWT com Expiração Curta (5 min):** Decidido para cumprir rigorosamente o item (b) do edital. A expiração curta minimiza a janela de uso de um token interceptado.
- **Refresh Token Pattern:** Implementado para garantir que o usuário não seja deslogado a cada 5 minutos. O sistema renova o Access Token de forma transparente, mantendo a arquitetura **Stateless** (sem necessidade de sessões no servidor), o que facilita a escalabilidade horizontal.

### 2. Gestão de Tráfego e Resiliência (Requisito Sênior C)
- **Rate Limiting (10 req/min):** Aplicado no nível da aplicação via `Filter`. O racional é proteger o banco de dados e o processamento de álbuns (pesados devido ao MinIO) contra abusos ou ataques de DoS simples, garantindo disponibilidade para todos os usuários.

### 3. Otimização de I/O e Storage (Requisito G)
- **Upload Direto via Presigned URLs:** Em vez de receber os bytes da imagem no backend e depois enviar ao MinIO (Double Hop), o backend gera uma URL autorizada. 
    - **Por que?** Isso economiza memória RAM e CPU do servidor de aplicação, permitindo que o cliente faça o upload diretamente para o Storage. O backend atua apenas como o orquestrador da segurança.
- **URLs Temporárias (30 min):** As imagens não possuem links públicos permanentes. Elas são assinadas sob demanda, garantindo que o acesso aos arquivos seja controlado e expire rapidamente.

### 4. Integridade de Dados e Sincronização (Requisito E)
- **Sincronização de Regionais (API SEPLAG):** 
    - **Estratégia de Update:** O sistema busca registros novos, atualiza os existentes e **inativa** (soft-delete) os que não constam mais na API externa.
    - **Por que?** Inativar em vez de excluir protege a integridade referencial de artistas e álbuns que já estavam vinculados a essas regionais no passado.

### 5. Arquitetura de Domínio e N:N
- **Relacionamento Bidirecional:** Álbuns e Artistas possuem uma relação de muitos-para-muitos. 
- **Solução de Recursividade (Bugfix):** Durante os testes, identificamos um `StackOverflowError` causado pelo Lombok na geração de `hashCode/equals`. A decisão foi excluir explicitamente as coleções do cálculo de identidade do objeto para permitir o carregamento Lazy e a persistência circular estável do Hibernate.

---

## 🛠 Como Executar

O projeto está totalmente conteinerizado. Siga os passos abaixo:

### 1. Clonar e Iniciar Infraestrutura
```bash
docker-compose build
docker-compose up -d
```

### 2. Acessar a Aplicação
- **Backend (API):** `http://localhost:8080/api/v1/`
- **Swagger:** `http://localhost:8080/swagger-ui.html`
- **Health Checks:** `http://localhost:8080/actuator/health`

### 3. Credenciais de Teste (Seed Data)
O sistema inicia com dados pré-carregados (Flyway):
- **Admin:** `admin@email.com` / `admin123`
- **Artistas Base:** Serj Tankian, Michel Teló, System of a Down, etc.

---

## 🔍 O que foi implementado (Aderência ao Edital)

| Requisito | Status | Comentário |
| :--- | :---: | :--- |
| **Autenticação JWT (5min)** | ✅ | Com Refresh Token funcional. |
| **Regional Synchronization** | ✅ | Sincronização automática via @Scheduled. |
| **Rate Limiting (10 req/min)**| ✅ | Proteção ativa nos endpoints. |
| **WebSocket Notifications** | ✅ | Notifica novos álbuns e novas capas. |
| **MinIO S3 Integration** | ✅ | Com Presigned URLs para upload. |
| **Relacionamento N:N** | ✅ | CRUD completo de Álbuns e Artistas. |
| **Filtros por Tipo** | ✅ | `?tipoArtista=CANTOR` ou `BANDA`. |
| **Flyway Migrations** | ✅ | Scripts V1 a V5 documentados. |
| **Health Checks** | ✅ | Actuator Liveness/Readiness configurados. |

---

## 📌 O que não foi implementado / Melhorias Futuras
- **Interface Frontend Completa:** O foco foi 100% no core backend e requisitos sênior.
- **HTTPS em Produção:** Requer configuração de certificados (SSL/TLS) no Nginx Gateway.
- **Cache com Redis:** Poderia ser adicionado para otimizar as consultas de regionais sincronizadas.

---

## 🧪 Testes Automatizados
O projeto possui 100% de cobertura nas regras de negócio críticas.
```bash
mvn test
```

---
Desenvolvido com foco em excelência técnica para o processo SEPLAG/IOMAT.
