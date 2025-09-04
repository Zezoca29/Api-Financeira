PHONY: build run stop clean logs test

# Default environment
ENV ?= dev

# Build the application
build:
	./mvnw clean package -DskipTests

# Build Docker image
docker-build: build
	docker build -t financial-api:latest .

# Run with Docker Compose
run:
	docker-compose up -d
	@echo "Services starting..."
	@echo "API will be available at: http://localhost:8080"
	@echo "Swagger UI: http://localhost:8080/swagger-ui.html"
	@echo "Grafana: http://localhost:3000 (admin/admin123)"
	@echo "Prometheus: http://localhost:9090"

# Stop all services
stop:
	docker-compose down

# Stop and remove volumes
clean:
	docker-compose down -v
	docker system prune -f

# View logs
logs:
	docker-compose logs -f financial-api

# Run tests
test:
	./mvnw test

# Database migration
migrate:
	./mvnw flyway:migrate

# Generate sample data
seed-data:
	curl -X POST http://localhost:8080/api/admin/seed-data \
		-H "X-API-Key: demo-api-key-12345"

# Health check
health:
	curl http://localhost:8080/api/health

# Performance test
load-test:
	@echo "Running basic load test..."
	for i in {1..100}; do \
		curl -s -H "X-API-Key: demo-api-key-12345" \
			http://localhost:8080/api/assets/PETR4/quote > /dev/null; \
	done
	@echo "Load test completed"

# Development setup
dev-setup:
	@echo "Setting up development environment..."
	cp .env.example .env
	./mvnw dependency:resolve
	docker-compose -f docker-compose.dev.yml up -d postgres redis
	@echo "Development setup completed"