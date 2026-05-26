# How `.env` for sensitive credentials

This document explains how to run the docker containers using with environment variables for credentials.

---

## 1. `.env` File

Create a `.env` file in your project root:

```
# MySQL configuration
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_DATABASE=mydb
MYSQL_USER=myuser
MYSQL_PASSWORD=mypass

# Spring Boot datasource
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/mydb
SPRING_DATASOURCE_USERNAME=myuser
SPRING_DATASOURCE_PASSWORD=mypass
```

> Keep `.env` out of version control if it contains sensitive credentials.

---

## 2. Docker Compose

Updated to use environment variables from `.env`:

```yaml
services:
  mysql-db:
    image: mysql:latest
    container_name: mysql-db
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - mysql-data:/var/lib/mysql

  spring-app:
    build: .
    container_name: spring-app
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
    depends_on:
      - mysql-db

volumes:
  mysql-data:
```

---

## 3. Spring Boot Configuration

Update `application.properties` to use environment variables:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> The app automatically picks up the values from the `.env` file when running in Docker.

---

## 4. Best Practices

* Keep `.env` out of version control if it contains sensitive data.
* Only expose MySQL to host in development; in production, keep it internal.

---