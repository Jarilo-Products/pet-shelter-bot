# PET SHELTER TELEGRAM BOT

Телеграм-бот, который сможет отвечать на популярные вопросы людей о том, что нужно знать и уметь, чтобы забрать животное из приюта.

## Состав команды
- Епифанова Валерия
- Кочерга Ольга
- Назаров Артем
- Тыщенко Евгений

## Стек технологий
- Spring Boot
- PostgreSQL
- Liquibase

## Команды для работы с докером

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
