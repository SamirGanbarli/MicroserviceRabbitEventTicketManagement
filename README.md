🎟️ Event Ticket Sales System - Microservices Architecture (Basic System for Practice)

This project is a microservices-based event ticket sales system built with Spring Boot. Users can register, log in, create events, purchase tickets, and process refunds. The system is designed with scalability, security, and asynchronous communication in mind.

🔧 Tech Stack:

Spring Boot (Microservices)
RabbitMQ (Asynchronous Messaging)
JWT (Authentication & Authorization)
PostgreSQL (Database)
Docker (Containerization)

🏗️ Microservices:

User Service – Handles authentication & user management.
Event Service – Manages event creation, updates, and deletion (Admin only).
Ticket Service – Handles ticket purchases & refunds.
Payment Service – Simulates payment processing.

🔄 Communication Flow:

RabbitMQ for async messaging.
JWT for secure authentication.
PostgreSQL for data persistence.

📈 Future Enhancements:
Implement SAGA Pattern for transaction consistency.
Integrate real payment gateways.
Deploy with Kubernetes (K8s) for scalability.
