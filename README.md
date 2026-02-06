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

### Decisões Técnicas

1.  **Arquitetura em Camadas:** Utilização do padrão Controller-Service-Repository para separação clara de responsabilidades.
2.  **Segurança (Requirement Senior A/B):** 
    - Implementação de **JWT com expiração de 5 minutos** (conforme edital) e fluxo de **Refresh Token** para continuidade da sessão.
    - **Rate Limiting:** Restrição de 10 requisições por minuto por usuário/IP para proteção contra ataques de força bruta ou DoS.
3.  **Upload Direto para S3 (Presigned URLs):** Para otimizar o backend, o sistema gera URLs pré-assinadas. O cliente faz o upload diretamente para o MinIO, reduzindo o tráfego de IO no servidor de aplicação.
4.  **Sincronização de Regionais (Requirement Senior E):** Implementado um serviço agendado que consome uma API externa, realiza o *de x para* de dados e sincroniza o banco de dados local (inativando registros ausentes e atualizando alterações).
5.  **Relacionamento N:N:** Persistência robusta entre Artistas e Álbuns com sincronização manual de ambos os lados da associação para garantir integridade no JPA.

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
- **Admin:** `admin@artistalbum.com` / `password123`
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
