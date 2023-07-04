# PET SHELTER TELEGRAM BOT

https://t.me/JP_pet_shelter_bot </br>
Телеграм-бот, который сможет отвечать на популярные вопросы людей о том, что нужно знать и уметь, чтобы забрать животное из приюта.

## Состав команды
- Епифанова Валерия
- Кочерга Ольга
- Назаров Артем
- Тыщенко Евгений

## Стек технологий
- Java 17, Maven
- Spring Boot 2.7.12, Spring Data JPA
- Pengrad Telegram Bot API
- PostgreSQL, H2 Database (tests), Liquibase
- Swagger
- Lombok

## Схема базы данных

![DB schema](/schema.png)

## Команды для работы с докером

### Собрать образы:
```bash
docker compose build --no-cache
```

### Собрать недостающие образы, запустить контейнеры:
```bash
docker compose up --detach
```

### Пересобрать и запустить приложение:
```bash
docker compose rm application --stop --force
docker compose up --build --no-deps --detach application
```

### Пересобрать и запустить базу данных:
```bash
docker compose rm database --stop --volumes --force
docker volume rm pet-shelter-bot_database-data --force
docker compose up --build --detach database
```
