# 🛒 E-Commerce Microservices Application

Application e-commerce complète basée sur une architecture microservices avec Spring Cloud et Angular 19.

---

## 📐 Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND                                    │
│                     Angular 19 (http://localhost:4200)                  │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         GATEWAY SERVICE                                  │
│              Spring Cloud Gateway + Resilience4j Circuit Breaker        │
│                        (http://localhost:8888)                          │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        ▼                           ▼                           ▼
┌───────────────┐         ┌───────────────┐         ┌───────────────┐
│   CUSTOMER    │         │   INVENTORY   │         │   BILLING     │
│   SERVICE     │         │   SERVICE     │         │   SERVICE     │
│   (8081)      │         │   (8082)      │         │   (8083)      │
└───────────────┘         └───────────────┘         └───────────────┘
        │                           │                           │
        └───────────────────────────┴───────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        ▼                           ▼                           ▼
┌───────────────┐         ┌───────────────┐         ┌───────────────┐
│   DISCOVERY   │         │    CONFIG     │         │   KEYCLOAK    │
│   SERVICE     │         │   SERVICE     │         │   (Auth)      │
│   (8761)      │         │   (9999)      │         │   (8080)      │
└───────────────┘         └───────────────┘         └───────────────┘
```

### Microservices

| Service | Description | Port |
|---------|-------------|------|
| **Discovery Service** | Netflix Eureka - Registre des services | 8761 |
| **Config Service** | Configuration centralisée | 9999 |
| **Gateway Service** | Point d'entrée unique + Circuit Breaker | 8888 |
| **Customer Service** | Gestion des clients | 8081 |
| **Inventory Service** | Gestion des produits et stocks | 8082 |
| **Billing Service** | Gestion des factures et commandes | 8083 |

---

## 🛠 Technologies

### Backend
- **Java** 21
- **Spring Boot** 3.5.7
- **Spring Cloud** 2025.0.0
- **Resilience4j** (Circuit Breaker)
- **OpenFeign** (Communication inter-services)
- **Spring Data REST** + **H2 Database**

### Frontend
- **Angular** 19
- **Signals** (State management réactif)
- **Keycloak-js** (Authentification OAuth2/OIDC)

### Infrastructure
- **Docker** & **Docker Compose**
- **Keycloak** 23.0.0 (Identity Provider)
- **PostgreSQL** 15 (pour Keycloak)
- **Apache Kafka** & **Zookeeper** (optionnel)

---

## 📋 Prerequisites

Avant de démarrer, assurez-vous d'avoir installé :

| Outil | Version | Vérification |
|-------|---------|--------------|
| **Docker Desktop** | Latest | `docker --version` |
| **Java JDK** | 21+ | `java -version` |
| **Node.js** | 18+ | `node --version` |
| **npm** | 10+ | `npm --version` |
| **Angular CLI** | 19+ | `ng version` |

---

## 🚀 Getting Started

### 1. Démarrer le Backend (Docker)

```bash
# Cloner le repository
git clone https://github.com/MokhtarLahjaily/ecom-app.git
cd ecomm-app-spring-cloud

# Démarrer tous les services
docker-compose up -d --build
```

⏳ **Attention** : Patientez environ **2-3 minutes** pour que tous les services soient opérationnels. Keycloak nécessite ~90 secondes pour démarrer.

```bash
# Vérifier l'état des services
docker-compose ps

# Attendre que tous les services soient "healthy"
watch docker-compose ps
```

### 2. Configurer Keycloak (Première fois uniquement)

1. Accéder à la console Keycloak : http://localhost:8080
2. Se connecter avec `admin` / `admin`
3. Créer un Realm : `ecom-realm`
4. Créer un Client : `ecom-app-frontend` (public, Standard flow)
5. Créer les rôles : `ADMIN`, `USER`
6. Créer les utilisateurs de test (voir tableau ci-dessous)

### 3. Démarrer le Frontend (Angular)

```bash
# Ouvrir un nouveau terminal
cd ../ecom-app-frontend

# Installer les dépendances
npm install

# Démarrer le serveur de développement
ng serve
```

---

## 🔗 URLs & Identifiants

### Services

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:4200 | Application Angular |
| **Gateway API** | http://localhost:8888 | API Gateway |
| **Eureka Dashboard** | http://localhost:8761 | Monitoring services |
| **Keycloak Console** | http://localhost:8080 | Administration IAM |
| **Config Server** | http://localhost:9999 | Configuration centralisée |

### Identifiants Keycloak Console

| Utilisateur | Mot de passe |
|-------------|--------------|
| `admin` | `admin` |

### Utilisateurs de Test (ecom-realm)

| Utilisateur | Mot de passe | Rôle | Permissions |
|-------------|--------------|------|-------------|
| `user1` | `1234` | USER | Voir produits, Ajouter au panier, Commander |
| `admin1` | `1234` | ADMIN | Tout + CRUD Produits |

---

## 📡 API Endpoints

Tous les endpoints sont accessibles via le Gateway (`http://localhost:8888`).

### Products (Inventory Service)
```
GET    /INVENTORY-SERVICE/api/products       # Liste des produits
GET    /INVENTORY-SERVICE/api/products/{id}  # Détail produit
POST   /INVENTORY-SERVICE/api/products       # Créer (ADMIN)
PUT    /INVENTORY-SERVICE/api/products/{id}  # Modifier (ADMIN)
DELETE /INVENTORY-SERVICE/api/products/{id}  # Supprimer (ADMIN)
```

### Customers (Customer Service)
```
GET    /CUSTOMER-SERVICE/api/customers       # Liste clients
GET    /CUSTOMER-SERVICE/api/customers/me    # Client courant
```

### Bills (Billing Service)
```
GET    /BILLING-SERVICE/bills/{id}           # Détail facture
GET    /BILLING-SERVICE/bills/search/by-user # Mes factures
POST   /BILLING-SERVICE/bills                # Créer commande
```

---

## 🔧 Configuration

La configuration est gérée par le Config Service depuis le dossier `config-repo/` :

- `application.properties` - Configuration commune
- `{service}-{profile}.properties` - Configuration par service et profil

---

## 🐳 Docker Commands

```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs d'un service
docker logs -f billing-service

# Redémarrer un service
docker-compose restart billing-service

# Arrêter tous les services
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

---

## ✨ Features

- ✅ **Authentification OAuth2/OIDC** avec Keycloak
- ✅ **Circuit Breaker** avec Resilience4j (fallbacks gracieux)
- ✅ **Validation des stocks** côté backend
- ✅ **Gestion d'erreurs RFC 7807** (ProblemDetail)
- ✅ **Health Checks Docker** avec dépendances
- ✅ **UI responsive** avec navigation hamburger
- ✅ **Toast notifications** non-bloquantes
- ✅ **Skeleton loading** pour meilleur UX
