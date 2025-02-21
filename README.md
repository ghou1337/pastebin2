# README

## Pastebin2 

### Description
Pastebin2 is a microservice application that allows users to store and retrieve text data with metadata, including expiration dates. The service can be deployed using Docker Compose for easy setup.
### Services
- **pastebin2-hash-generator**  
  API: http://localhost:9090
- **spring-boot-admin**  
  API: http://localhost:8081
- **pastebin2**
- API: http://localhost:8080

### Accessing the Service
Once started, the microservice is accessible at:
- http://localhost:8080

### Endpoints
- **POST /api/post** - Stores text data with a generated hash.
- **GET /api/{hash}** - Retrieves text data by hash.
- **GET /api/metadata/{hash}** - Retrieves metadata associated with stored text.

### Error Handling
Custom exceptions ensure structured error responses for invalid input, metadata saving issues, and missing data.

### Docker Compose Setup
To deploy the Pastebin2 service and its dependencies using Docker Compose, use the following `docker-compose.yml` file:

```yaml
version: '3.8'
name: pastebin2

services:
  postgres:
    image: postgres:15
    container_name: postgres_db
    restart: always
    environment:
      POSTGRES_DB: pastebin
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    networks:
      - pastebin_network
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7
    container_name: redis_cache
    restart: always
    networks:
      - pastebin_network
    volumes:
      - redis_data:/data

  pastebin2:
    build:
      context: ./pastebin2
      dockerfile: Dockerfile
    container_name: pastebin2_service
    restart: always
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres_db:5432/pastebin
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: password
      GOOGLE_CLOUD_STORAGE_BUCKET: pastebin-bucket
      GOOGLE_APPLICATION_CREDENTIALS: /secrets/gcloudkey.json
    networks:
      - pastebin_network
    volumes:
      - gcloud_secrets:/secrets:ro

  pastebin2-hash-generator:
    build:
      context: ./pastebin2-hash-generator
      dockerfile: Dockerfile
    container_name: pastebin2_hash_service
    restart: always
    depends_on:
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres_db:5432/pastebin
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: password
      REDIS_HOST: redis
      REDIS_PORT: 6379
    networks:
      - pastebin_network

  spring-boot-admin:
    build:
      context: ./admin
      dockerfile: Dockerfile
    container_name: spring-boot-admin
    restart: always
    networks:
      - pastebin_network

networks:
  pastebin_network:

volumes:
  postgres_data:
  redis_data:
  gcloud_secrets:
```

### Running the Service
To start the Pastebin2 service and its dependencies, run:

```sh
docker-compose up -d
```

To stop the service, use:

```sh
docker-compose down
```
