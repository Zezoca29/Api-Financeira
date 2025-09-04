# 📊 API de Análise Financeira em Tempo Real

> **Sistema distribuído e resiliente para análise de mercado financeiro com indicadores técnicos, cotações em tempo real e gerenciamento de transações.**

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue) ![Redis](https://img.shields.io/badge/Redis-7-red) ![Docker](https://img.shields.io/badge/Docker-Ready-blue)

## 🎯 **Visão Geral**

Esta API foi desenvolvida seguindo os princípios de **Clean Architecture** e **CQRS**, implementando características essenciais de sistemas financeiros de alta performance:

- ✅ **Autenticação por API Key** com rate limiting
- ✅ **Cache distribuído** com Redis para cotações
- ✅ **Circuit Breaker** para resiliência
- ✅ **Observabilidade** com Prometheus + Grafana
- ✅ **Indicadores técnicos** (RSI, SMA, Volatilidade)
- ✅ **Simulação de dados** em tempo real
- ✅ **Arquitetura escalável** e cloud-ready

## 🏗️ **Arquitetura**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │    │   Application   │    │     Domain      │
│                 │────│     Services    │────│    Services     │
│  • AssetAPI     │    │  • AssetService │    │ • Calculations  │
│  • IndicatorAPI │    │  • Indicators   │    │ • Validations   │
│  • Transaction  │    │  • Transactions │    │ • Business Rules│
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                    ┌─────────────────┐
                    │ Infrastructure  │
                    │                 │
                    │ • PostgreSQL    │
                    │ • Redis Cache   │
                    │ • Security      │
                    │ • Monitoring    │
                    └─────────────────┘
```

## 🚀 **Quick Start**

### **1. Clone e Configure**
```bash
git clone https://github.com/seu-usuario/financial-realtime-api.git
cd financial-realtime-api

# Copie as configurações
cp .env.example .env
```

### **2. Execute com Docker**
```bash
# Inicia todos os serviços
make run

# Ou manualmente
docker-compose up -d
```

### **3. Acesse os Serviços**

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| **API Principal** | http://localhost:8080 | API Key: `demo-api-key-12345` |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| **Grafana** | http://localhost:3000 | admin / admin123 |
| **Prometheus** | http://localhost:9090 | - |

## 📡 **Endpoints da API**

### **🔹 Cotações em Tempo Real**

```http
GET /api/assets/{ticker}/quote
Headers: X-API-Key: demo-api-key-12345

# Exemplo
curl -H "X-API-Key: demo-api-key-12345" \
  http://localhost:8080/api/assets/PETR4/quote
```

**Response:**
```json
{
  "ticker": "PETR4",
  "name": "Petrobras PN",
  "category": "STOCKS", 
  "currentPrice": 25.67,
  "previousClose": 25.50,
  "priceChange": 0.17,
  "priceChangePercent": 0.67,
  "lastUpdated": "2024-12-19T10:30:00",
  "source": "DATABASE"
}
```

### **🔹 Histórico de Preços**

```http
GET /api/assets/{ticker}/history?range=30d
```

**Ranges suportados:** `7d`, `30d`, `3m`, `1y`

### **🔹 Indicadores Técnicos**

#### **RSI (Relative Strength Index)**
```http
GET /api/indicators/rsi?ticker=PETR4&periods=14

Response:
{
  "ticker": "PETR4",
  "indicator": "RSI", 
  "value": 45.67,
  "periods": 14,
  "interpretation": "NEUTRAL - No strong signal",
  "calculatedAt": "2024-12-19T10:30:00"
}
```

#### **Média Móvel Simples (SMA)**
```http
GET /api/indicators/sma?ticker=PETR4&periods=20
```

#### **Volatilidade**
```http
GET /api/indicators/volatility?ticker=PETR4&periods=30
```

### **🔹 Transações**

#### **Criar Transação**
```http
POST /api/transactions
Content-Type: application/json
X-API-Key: demo-api-key-12345

{
  "userId": "user123",
  "ticker": "PETR4",
  "type": "BUY",
  "quantity": 100,
  "price": 25.50
}
```

#### **Relatório de Transações**
```http
GET /api/transactions/report/user123
```

**Response:**
```json
{
  "userId": "user123",
  "totalTransactions": 5,
  "totalBuyTransactions": 3,
  "totalSellTransactions": 2,
  "totalAmountBought": 7650.00,
  "totalAmountSold": 2550.00,
  "netAmount": -5100.00,
  "currentPositions": {
    "PETR4": 200,
    "VALE3": 50
  },
  "generatedAt": "2024-12-19T10:30:00"
}
```

## 📊 **Ativos Disponíveis**

### **📈 Ações Brasileiras**
- `PETR4` - Petrobras PN
- `VALE3` - Vale ON  
- `ITUB4` - Itaú Unibanco PN
- `BBDC4` - Bradesco PN
- `WEGE3` - WEG ON

### **₿ Criptomoedas**
- `BTC` - Bitcoin
- `ETH` - Ethereum
- `ADA` - Cardano

### **📊 Índices**
- `IBOV` - Ibovespa
- `IFIX` - Índice de FIIs

## 🛡️ **Segurança e Rate Limiting**

### **Autenticação**
```bash
# Todas as requisições precisam do header:
X-API-Key: demo-api-key-12345
```

### **Rate Limiting**
- **100 requisições por minuto** por API key
- **Resposta HTTP 429** quando limite excedido

### **Circuit Breaker**
- Ativa após **5 falhas consecutivas**
- **Timeout de 10 segundos** em estado aberto
- **Fallback automático** para dados em cache

## 🔧 **Tecnologias e Dependências**

### **Backend**
- **Java 17** + **Spring Boot 3.2**
- **PostgreSQL 15** (dados persistentes)
- **Redis 7** (cache distribuído)
- **Resilience4j** (circuit breaker)
- **Bucket4j** (rate limiting)

### **Observabilidade**
- **Prometheus** (métricas)
- **Grafana** (dashboards)
- **Spring Actuator** (health checks)
- **Micrometer** (instrumentação)

### **Documentação**
- **OpenAPI 3** + **Swagger UI**
- **Javadoc** completo
- **Testes automatizados**

## 🐳 **Docker & Deployment**

### **Desenvolvimento Local**
```bash
# Setup inicial
make dev-setup

# Build da aplicação
make build

# Executar todos os serviços
make run

# Logs em tempo real  
make logs

# Parar serviços
make stop

# Limpeza completa
make clean
```

### **Monitoramento**

#### **Health Check**
```bash
curl http://localhost:8080/api/health
```

#### **Métricas Prometheus**
```bash
curl http://localhost:8080/actuator/prometheus
```

#### **Dashboards Grafana**
- **API Performance** - Response times, throughput
- **Database Metrics** - Conexões, queries, cache hit rate
- **Business Metrics** - Transações, cotações, usuários ativos
- **System Health** - CPU, memória, disk I/O

## 📈 **Demonstrações de Performance**

### **Load Test Básico**
```bash
# Executa 100 requisições simultâneas
make load-test

# Ou manual com curl
for i in {1..100}; do
  curl -s -H "X-API-Key: demo-api-key-12345" \
    http://localhost:8080/api/assets/PETR4/quote &
done
wait
```

### **Cache Performance**
- **Primera consulta:** ~200ms (database)
- **Consultas subsequentes:** ~5ms (Redis cache)
- **Cache TTL:** 30 segundos para cotações

### **Circuit Breaker Demo**
```bash
# Simular falha no banco
docker-compose pause postgres

# API continua funcionando com fallback
curl -H "X-API-Key: demo-api-key-12345" \
  http://localhost:8080/api/assets/PETR4/quote
```

## 🚀 **Escalabilidade**

### **Horizontal Scaling**
```yaml
# docker-compose.scale.yml
services:
  financial-api:
    deploy:
      replicas: 3
  
  nginx:
    image: nginx:alpine
    ports:
      - "80:80" 
    # Load balancer configuration
```

### **Database Sharding**
```sql
-- Particionamento por ticker
CREATE TABLE price_history_stocks PARTITION OF price_history
FOR VALUES IN ('PETR4', 'VALE3', 'ITUB4');

CREATE TABLE price_history_crypto PARTITION OF price_history  
FOR VALUES IN ('BTC', 'ETH', 'ADA');
```

## 🧪 **Testes**

### **Executar Testes**
```bash
# Todos os testes
make test

# Testes de integração com TestContainers
./mvnw test -Dtest=IntegrationTest

# Testes de performance
./mvnw test -Dtest=PerformanceTest
```

### **Coverage Report**
```bash
./mvnw jacoco:report
open target/site/jacoco/index.html
```

## 🌐 **Deploy em Produção**

### **Railway / Render / Fly.io**
```bash
# Build para produção
./mvnw clean package -Pprod

# Deploy no Railway
railway up

# Deploy no Render  
render deploy

# Deploy no Fly.io
flyctl deploy
```

### **Kubernetes**
```yaml
# k8s/deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: financial-api
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: api
        image: financial-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
```

## 🔍 **Troubleshooting**

### **Problemas Comuns**

**API retorna 429 (Rate Limit)**
```bash
# Verificar rate limit atual
redis-cli get "api:demo-api-key-12345"
```

**Circuit breaker ativo**
```bash
# Verificar health do banco
curl http://localhost:8080/actuator/health
```

**Cache miss alto**
```bash
# Verificar conexão Redis
redis-cli ping
```

### **Logs Úteis**
```bash
# Logs da aplicação
docker-compose logs financial-api

# Logs do banco
docker-compose logs postgres

# Logs do Redis
docker-compose logs redis
```

## 📚 **Documentação Adicional**

- **[API Reference](http://localhost:8080/swagger-ui.html)** - Documentação interativa
- **[Architecture Decision Records](docs/adr/)** - Decisões arquiteturais
- **[Performance Benchmarks](docs/performance.md)** - Testes de performance
- **[Deployment Guide](docs/deployment.md)** - Guia de deploy completo

## 🤝 **Contribuição**

1. **Fork** o projeto
2. **Crie** uma feature branch (`git checkout -b feature/nova-funcionalidade`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. **Push** para a branch (`git push origin feature/nova-funcionalidade`)
5. **Abra** um Pull Request

## 📄 **Licença**

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 🎯 **Próximos Passos**

- [ ] **WebSocket** para cotações em tempo real
- [ ] **Machine Learning** para predição de preços  
- [ ] **GraphQL** API alternativa
- [ ] **Multi-tenancy** para diferentes clientes
- [ ] **Backup automatizado** com point-in-time recovery
- [ ] **A/B testing** framework integrado

---

**⭐ Se este projeto foi útil, deixe uma estrela no GitHub!**