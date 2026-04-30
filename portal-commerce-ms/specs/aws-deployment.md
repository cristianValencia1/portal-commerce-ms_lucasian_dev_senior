# AWS Deployment Strategy — portal-commerce-ms

## 1. Objetivo
Definir una arquitectura de despliegue en AWS para la solución basada en microservicios (sales y shipping), garantizando escalabilidad, resiliencia, seguridad y observabilidad.

---

## 2. Arquitectura propuesta

Componentes principales:

- Amazon ECS Fargate (contenedores)
- Amazon ECR (imágenes Docker)
- Amazon RDS PostgreSQL
- Amazon MQ (RabbitMQ)
- Application Load Balancer (ALB)
- AWS CloudWatch
- AWS X-Ray
- AWS Secrets Manager
- VPC privada

---

## 3. Flujo

Cliente → ALB → Microservicios (ECS) → RDS  
Microservicios → RabbitMQ → Microservicios

---

## 4. Microservicios

### Sales Service
- API REST
- Publica eventos

### Shipping Service
- Consume eventos
- Procesa envíos

---

## 5. Persistencia

- RDS PostgreSQL
- Schemas:
  - sales
  - shipping

---

## 6. Mensajería

- Amazon MQ (RabbitMQ)
- Alternativa: SQS/SNS

---

## 7. Seguridad

- VPC privada
- Security Groups
- Secrets Manager
- TLS

---

## 8. Escalabilidad

- Auto Scaling ECS
- Escalado independiente por servicio

---

## 9. Observabilidad

- CloudWatch Logs
- X-Ray tracing

---

## 10. Resiliencia

- Outbox Pattern
- Idempotencia
- Reintentos

---

## 11. CI/CD

- GitHub Actions o CodePipeline
- Deploy a ECS

---

## 12. Costos

- ECS Fargate
- RDS
- MQ

Optimización:
- Auto scaling
- Ambientes diferenciados

---

## 13. Trade-offs

- RabbitMQ vs SQS
- PostgreSQL vs NoSQL
- Fargate vs EC2

---

## 14. Riesgos

- duplicación de eventos
- latencia

Mitigación:
- idempotencia
- monitoreo

---

## 15. Evolución

- Kafka / Kinesis
- Saga Pattern
- OAuth2

---

## 16. Conclusión

Arquitectura escalable, resiliente y alineada a cloud-native.
