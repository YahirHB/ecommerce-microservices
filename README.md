# ecommerce-microservices

Plataforma de e-commerce construida con arquitectura de microservicios usando Spring Boot. Implementa comunicación síncrona REST y asíncrona con Kafka, consistencia eventual mediante el patrón Saga coreografiado y gestión de inventario con reservas temporales por orden.

---

## Arquitectura general

```
Client
  │
  ▼
API Gateway (Spring Cloud Gateway)
  │
  ├──► Product Service     (Hexagonal Architecture)
  ├──► Inventory Service   (N-Layer · Kafka)
  ├──► Order Service       (Hexagonal Architecture · Saga)
  └──► Payment Service     (Hexagonal Architecture · Kafka)

Discovery Server — Eureka
Config Server    — Spring Cloud Config
```

### Flujo de comunicación asíncrona (Saga coreografiado)

```
Order Service
  │  publica: order.created
  ▼
Payment Service — escucha: order.created
  │  publica: payment.completed | payment.failed
  ▼
Order Service — escucha: payment.completed → CONFIRMED
             — escucha: payment.failed     → CANCELLED
  │  publica: order.status.changed
  ▼
Inventory Service — escucha: order.status.changed
  CONFIRMED → confirma reservas → descuenta stock real
  CANCELLED → libera reservas  → stock disponible de nuevo
```

---

## Servicios

### Product Service
Gestión del catálogo de productos. Sin stock — esa responsabilidad fue migrada completamente a Inventory Service.

**Arquitectura:** Hexagonal (Ports & Adapters)
**Base URL:** `/products`
**Kafka:** no produce ni consume eventos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/products` | Crear producto |
| PUT | `/products/{id}` | Actualizar producto |
| PATCH | `/products/{id}/deactivate` | Desactivar producto |
| PATCH | `/products/{id}/restore` | Restaurar producto |
| DELETE | `/products/{id}` | Eliminar producto |
| GET | `/products/{id}` | Buscar por ID |
| GET | `/products/sku/{sku}` | Buscar por SKU |
| GET | `/products` | Listar paginado |
| GET | `/products/category/{categoryId}` | Filtrar por categoría |

---

### Inventory Service
Gestión de stock con reservas temporales por orden. El stock nunca se descuenta al crear la orden — se reserva y se descuenta únicamente al confirmar el pago.

**Arquitectura:** N-Layer (Controller → Service → Repository → Entity)
**Base URL:** `/api/v1/inventory`
**Tablas:** `inventory`, `stock_reservations`
**Comunicación saliente:** RestTemplate a Product Service con Circuit Breaker + Retry (Resilience4j)

```
availableQuantity = stockQuantity - reservedQuantity
```

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/inventory` | Crear registro de inventario |
| GET | `/api/v1/inventory/product/{productId}` | Consultar inventario |
| GET | `/api/v1/inventory/product/{productId}/summary` | Resumen de stock disponible |
| POST | `/api/v1/inventory/reserve` | Reservar stock de un producto |
| POST | `/api/v1/inventory/reserve/all` | Reservar stock de todos los items de una orden |
| POST | `/api/v1/inventory/release` | Liberar reserva individual |
| PUT | `/api/v1/inventory/confirm/order/{orderId}` | Confirmar todas las reservas de una orden |
| PUT | `/api/v1/inventory/release/order/{orderId}` | Liberar todas las reservas de una orden |
| PUT | `/api/v1/inventory/adjust` | Ajustar stock manualmente |
| GET | `/api/v1/inventory/low-stock` | Productos con stock bajo |

**Kafka — publica:**

| Topic | Cuándo |
|-------|--------|
| `stock.reserved` | Al reservar stock de un producto |
| `stock.released` | Al liberar una reserva |
| `stock.low-alert` | Al detectar stock bajo |

**Kafka — escucha:**

| Topic | Acción |
|-------|--------|
| `order.status.changed` | `CONFIRMED` → confirma reservas y descuenta stock real |
| `order.status.changed` | `CANCELLED` → libera todas las reservas de la orden |

---

### Order Service
Gestión del ciclo de vida de órdenes. Coordina con Inventory y Product mediante puertos de dominio (Anti-Corruption Layer).

**Arquitectura:** Hexagonal (Ports & Adapters)
**Base URL:** `/api/v1/orders`
**Clientes:** `ProductFeignClient` (snapshot precio), `InventoryFeignClient` (stock y reservas)

**Estados de una orden:**
```
PENDING → CONFIRMED
        ↘ CANCELLED
```

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/orders` | Crear orden |
| GET | `/api/v1/orders/{id}` | Buscar por ID |
| GET | `/api/v1/orders` | Listar todas paginado |
| GET | `/api/v1/orders/customerId/{customerId}` | Órdenes por cliente |
| PATCH | `/api/v1/orders/{id}/status` | Actualizar estado |

**Flujo `createOrder`:**
```
1. Valida stock disponible por item        → Inventory (getStockSummary)
2. Obtiene snapshot de precio              → Product (getProduct)
3. Persiste Order en estado PENDING
4. Reserva todos los items en una llamada  → Inventory (reserveAllStock)
   └── si falla → Order pasa a CANCELLED
5. Publica order.created                   → Kafka
```

**Kafka — publica:**

| Topic | Cuándo |
|-------|--------|
| `order.created` | Al crear una orden en PENDING |
| `order.status.changed` | Cada vez que cambia el estado de la orden |

**Kafka — escucha:**

| Topic | Acción |
|-------|--------|
| `payment.completed` | Actualiza orden a `CONFIRMED` → publica `order.status.changed` |
| `payment.failed` | Actualiza orden a `CANCELLED` → publica `order.status.changed` |

---

### Payment Service
Procesamiento de pagos. Actualmente con simulación de pasarela — extensible a Stripe o PayPal mediante `PaymentGatewayPort`.

**Arquitectura:** Hexagonal (Ports & Adapters)
**Base URL:** `/api/v1/payments`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/payments` | Procesar pago manualmente |
| GET | `/api/v1/payments/{id}` | Buscar por ID |
| GET | `/api/v1/payments/order/{orderId}` | Buscar por orden |

**Simulación de pago:**
> Si el monto es exactamente `1998.00` el pago falla. Cualquier otro monto es exitoso. Esto permite probar ambos flujos sin integración real.

**Kafka — publica:**

| Topic | Cuándo |
|-------|--------|
| `payment.completed` | Al procesar un pago exitoso |
| `payment.failed` | Al fallar el procesamiento del pago |

**Kafka — escucha:**

| Topic | Acción |
|-------|--------|
| `order.created` | Inicia el procesamiento del pago automáticamente |

---

## Stack tecnológico

| Categoría | Tecnología |
|-----------|------------|
| Framework | Spring Boot 3.2 · Java 17 |
| Arquitecturas | Hexagonal (Product, Order, Payment) · N-Layer (Inventory) |
| Comunicación síncrona | OpenFeign · RestTemplate |
| Comunicación asíncrona | Apache Kafka |
| Persistencia | Spring Data JPA · MySQL |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Configuración centralizada | Spring Cloud Config |
| Batch processing | Spring Batch |
| Resiliencia | Resilience4j — Circuit Breaker · Retry |
| Documentación | SpringDoc OpenAPI · Swagger UI |
| Seguridad | Spring Security (en desarrollo) |
| Testing | JUnit 5 · Mockito |
| Build | Maven |
| Contenedores | Docker · Docker Compose (Kafka) |

---

## Patrones aplicados

| Patrón | Servicio | Descripción |
|--------|----------|-------------|
| Hexagonal Architecture | Product · Order · Payment | Separación estricta de dominio, puertos e infraestructura |
| N-Layer Architecture | Inventory | Controller → Service → Repository → Entity |
| Saga — Choreography | Order · Payment · Inventory | Consistencia eventual sin orquestador central |
| CQRS parcial | Inventory | Commands (escritura con Lombok) · Records (lectura inmutable) |
| Snapshot Pattern | Order | Precio guardado en `order_items` al momento de la compra |
| TTL-based Reservation | Inventory | Reservas con expiración automática, liberación por orden completa |
| Circuit Breaker | Inventory → Product | Resilience4j previene fallos en cascada |
| Compensating Transaction | Inventory | Libera stock si la orden es cancelada |
| Anti-Corruption Layer | Order · Payment | Feign clients y ports aíslan contratos entre servicios |
| Event-Driven Architecture | Todos | Kafka desacopla productores y consumidores |
| Optimistic Locking | Inventory | `@Version` en `StockReservation` previene condiciones de carrera |

---

## Estructura del repositorio

```
ecommerce-microservices/
├── infrastructure/
│   ├── api-gateway/
│   └── discovery-server/
├── services/
│   ├── product-service/
│   ├── inventory-service/
│   ├── order-service/
│   └── payment-service/
├── batch/
├── docker/
├── docs/
├── scripts/
└── docker-compose.yml
```

---

## Cómo ejecutar

### Prerrequisitos
- Java 17+
- Maven 3.8+
- Docker y Docker Compose

### 1. Levantar Kafka

```bash
docker-compose up -d
```

### 2. Levantar servicios en orden

```bash
# 1. Discovery Server
cd infrastructure/discovery-server && mvn spring-boot:run

# 2. Servicios (en paralelo)
cd services/product-service   && mvn spring-boot:run
cd services/inventory-service && mvn spring-boot:run
cd services/order-service     && mvn spring-boot:run
cd services/payment-service   && mvn spring-boot:run
```

### 3. Swagger UI por servicio

| Servicio | URL |
|----------|-----|
| Product | http://localhost:8081/swagger-ui.html |
| Inventory | http://localhost:8083/swagger-ui.html |
| Order | http://localhost:8082/swagger-ui.html |
| Payment | http://localhost:8084/swagger-ui.html |
| Eureka Dashboard | http://localhost:8761 |

---

## En desarrollo

- [ ] Auth Service — JWT · Spring Security · OAuth2
- [ ] User Service — Perfil · Direcciones · Roles
- [ ] Cart Service — Items efímeros con TTL
- [ ] Checkout Service — Orquestador del flujo de compra
- [ ] Notification Service — Email · Push · Kafka consumer
- [ ] Integración completa con API Gateway y Config Server
- [ ] Docker Compose multi-servicio completo

---

## Autor

**Yahir** — Backend Developer  
Stack: Java · Spring Boot · Microservices · Kafka  
GitHub: [@YahirHB](https://github.com/YahirHB)
